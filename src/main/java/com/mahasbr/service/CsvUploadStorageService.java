package com.mahasbr.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.config.FileStorageProperties;
import com.mahasbr.entity.CsvUploadJobEntity;
import com.mahasbr.validators.FileValidator;

@Service
public class CsvUploadStorageService {

	private static final String PREVIEW_DIR = "preview";
	private static final String PREVIEW_PAGES_DIR = "pages";
	private static final String VALID_RECORDS_FILE = "valid-records.jsonl";
	private static final String INVALID_RECORDS_FILE = "invalid-records.jsonl";
	private static final String FAILED_PREVIEW_RECORDS_FILE = "failed-preview-records.jsonl";

	private final Path rootDirectory;
	private final ObjectMapper objectMapper;
	private final FileValidator fileValidator;

	public CsvUploadStorageService(FileStorageProperties properties, ObjectMapper objectMapper, FileValidator fileValidator)
			throws IOException {
		this.objectMapper = objectMapper;
		this.fileValidator = fileValidator;
		String uploadDir = StringUtils.hasText(properties.getUploadDir()) ? properties.getUploadDir() : "./uploads";
		this.rootDirectory = Paths.get(uploadDir).toAbsolutePath().normalize().resolve("csv-upload-jobs");
		Files.createDirectories(rootDirectory);
	}

	public String storeUploadedFile(String jobId, MultipartFile file, FileUploadPolicy policy) {
		fileValidator.validate(file, policy);
		String extension = extractExtension(file.getOriginalFilename());
		String storedFileName = "input" + (extension.isBlank() ? "" : "." + extension);
		Path targetDirectory = jobDirectory(jobId);

		try {
			Files.createDirectories(targetDirectory);
			try (var inputStream = file.getInputStream()) {
				Files.copy(inputStream, targetDirectory.resolve(storedFileName), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to store uploaded file.", exception);
		}

		return storedFileName;
	}

	public void resetPreviewArtifacts(String jobId) {
		deleteIfExists(previewDirectory(jobId));
	}

	public Path sourceFilePath(CsvUploadJobEntity job) {
		return jobDirectory(job.getJobId()).resolve(job.getStoredFileName());
	}

	public BufferedWriter newValidRowsWriter(String jobId) throws IOException {
		Path target = previewDirectory(jobId).resolve(VALID_RECORDS_FILE);
		Files.createDirectories(target.getParent());
		return Files.newBufferedWriter(target, StandardCharsets.UTF_8);
	}

	public BufferedWriter newInvalidRowsWriter(String jobId) throws IOException {
		Path target = previewDirectory(jobId).resolve(INVALID_RECORDS_FILE);
		Files.createDirectories(target.getParent());
		return Files.newBufferedWriter(target, StandardCharsets.UTF_8);
	}

	public Path validRowsPath(String jobId) {
		return previewDirectory(jobId).resolve(VALID_RECORDS_FILE);
	}

	public Path invalidRowsPath(String jobId) {
		return previewDirectory(jobId).resolve(INVALID_RECORDS_FILE);
	}

	public Path failedPreviewRowsPath(String jobId) {
		return previewDirectory(jobId).resolve(FAILED_PREVIEW_RECORDS_FILE);
	}

	public Path previewPagesDirectory(String jobId) {
		return previewDirectory(jobId).resolve(PREVIEW_PAGES_DIR);
	}

	public <T> List<T> readPage(Path pageDirectory, int pageNumber, TypeReference<List<T>> typeReference) {
		Path pagePath = pageDirectory.resolve("page-" + String.format("%06d", pageNumber + 1) + ".json");
		if (!Files.exists(pagePath)) {
			return List.of();
		}

		try {
			return objectMapper.readValue(pagePath.toFile(), typeReference);
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to read paged upload data.", exception);
		}
	}

	public void appendJsonLine(BufferedWriter writer, Object value) throws IOException {
		writer.write(objectMapper.writeValueAsString(value));
		writer.newLine();
	}

	public <T> void streamJsonLines(Path filePath, Class<T> type, JsonLineConsumer<T> consumer) throws IOException {
		if (!Files.exists(filePath)) {
			return;
		}

		try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isBlank()) {
					continue;
				}
				consumer.accept(objectMapper.readValue(line, type));
			}
		}
	}

	public <T> JsonLinesPage<T> readJsonLinesPage(Path filePath, int pageNumber, int pageSize, Class<T> type) {
		if (!Files.exists(filePath)) {
			return new JsonLinesPage<>(List.of(), 0);
		}

		int safePageNumber = Math.max(pageNumber, 0);
		int safePageSize = Math.max(pageSize, 1);
		int startIndex = safePageNumber * safePageSize;
		int endIndex = startIndex + safePageSize;
		int index = 0;
		List<T> records = new java.util.ArrayList<>(safePageSize);

		try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isBlank()) {
					continue;
				}
				if (index >= startIndex && index < endIndex) {
					records.add(objectMapper.readValue(line, type));
				}
				index++;
			}
			return new JsonLinesPage<>(records, index);
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to read upload result data.", exception);
		}
	}

	private Path jobDirectory(String jobId) {
		return rootDirectory.resolve(jobId).normalize();
	}

	private Path previewDirectory(String jobId) {
		return jobDirectory(jobId).resolve(PREVIEW_DIR);
	}

	private void deleteIfExists(Path path) {
		if (!Files.exists(path)) {
			return;
		}

		try (var walk = Files.walk(path)) {
			walk.sorted((left, right) -> right.compareTo(left)).forEach(current -> {
				try {
					Files.deleteIfExists(current);
				} catch (IOException exception) {
					throw new IllegalStateException("Unable to clear upload artifacts.", exception);
				}
			});
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to clear upload artifacts.", exception);
		}
	}

	private String extractExtension(String fileName) {
		if (!StringUtils.hasText(fileName) || !fileName.contains(".")) {
			return "";
		}
		return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
	}

	@FunctionalInterface
	public interface JsonLineConsumer<T> {
		void accept(T value) throws IOException;
	}

	public record JsonLinesPage<T>(List<T> records, int totalRecords) {
	}
}
