package com.mahasbr.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.config.FileStorageProperties;
import com.mahasbr.validators.FileValidator;

@Component
public class CsvUploadSessionStore {

	private static final String METADATA_FILE = "metadata.json";
	private static final String PREVIEW_DIR = "preview";
	private static final String RESULTS_DIR = "results";
	private static final String PREVIEW_PAGES_DIR = "pages";
	private static final String SUCCESS_PAGES_DIR = "success-pages";
	private static final String FAILED_PAGES_DIR = "failed-pages";
	private static final String VALID_RECORDS_FILE = "valid-records.jsonl";
	private static final String INVALID_RECORDS_FILE = "invalid-records.jsonl";

	private final Path rootDirectory;
	private final ObjectMapper objectMapper;
	private final FileValidator fileValidator;
	private final Map<String, CsvUploadSession> cache = new ConcurrentHashMap<>();

	public CsvUploadSessionStore(FileStorageProperties properties, ObjectMapper objectMapper, FileValidator fileValidator)
			throws IOException {
		this.objectMapper = objectMapper;
		this.fileValidator = fileValidator;
		String uploadDir = StringUtils.hasText(properties.getUploadDir()) ? properties.getUploadDir() : "./uploads";
		this.rootDirectory = Paths.get(uploadDir).toAbsolutePath().normalize().resolve("csv-upload-sessions");
		Files.createDirectories(rootDirectory);
	}

	public CsvUploadSession createSession(MultipartFile file, FileUploadPolicy policy) {
		fileValidator.validate(file, policy);
		String uploadId = UUID.randomUUID().toString();
		String extension = extractExtension(file.getOriginalFilename());
		String storedFileName = "input" + (extension.isBlank() ? "" : "." + extension);
		Path sessionDirectory = sessionDirectory(uploadId);

		try {
			Files.createDirectories(sessionDirectory);
			try (var inputStream = file.getInputStream()) {
				Files.copy(inputStream, sessionDirectory.resolve(storedFileName), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to store uploaded file.", exception);
		}

		CsvUploadSession session = CsvUploadSession.builder().uploadId(uploadId)
				.fileName(file.getOriginalFilename() == null ? "upload" : file.getOriginalFilename())
				.fileSize(file.getSize()).storedFileName(storedFileName).status(CsvUploadLifecycleStatus.UPLOADED)
				.message("File uploaded successfully.").previewPageSize(100).resultPageSize(100).createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now()).build();
		saveSession(session);
		return session;
	}

	public CsvUploadSession getRequiredSession(String uploadId) {
		return cache.computeIfAbsent(uploadId, this::loadSession);
	}

	public void saveSession(CsvUploadSession session) {
		session.setUpdatedAt(LocalDateTime.now());
		cache.put(session.getUploadId(), session);
		try {
			Files.createDirectories(sessionDirectory(session.getUploadId()));
			objectMapper.writeValue(metadataPath(session.getUploadId()).toFile(), session);
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to persist upload session metadata.", exception);
		}
	}

	public void resetPreviewArtifacts(String uploadId) {
		deleteIfExists(previewDirectory(uploadId));
	}

	public void resetResultArtifacts(String uploadId) {
		deleteIfExists(resultsDirectory(uploadId));
	}

	public Path sourceFilePath(String uploadId) {
		CsvUploadSession session = getRequiredSession(uploadId);
		return sessionDirectory(uploadId).resolve(session.getStoredFileName());
	}

	public BufferedWriter newValidRowsWriter(String uploadId) throws IOException {
		Path target = previewDirectory(uploadId).resolve(VALID_RECORDS_FILE);
		Files.createDirectories(target.getParent());
		return Files.newBufferedWriter(target, StandardCharsets.UTF_8);
	}

	public BufferedWriter newInvalidRowsWriter(String uploadId) throws IOException {
		Path target = previewDirectory(uploadId).resolve(INVALID_RECORDS_FILE);
		Files.createDirectories(target.getParent());
		return Files.newBufferedWriter(target, StandardCharsets.UTF_8);
	}

	public Path validRowsPath(String uploadId) {
		return previewDirectory(uploadId).resolve(VALID_RECORDS_FILE);
	}

	public Path invalidRowsPath(String uploadId) {
		return previewDirectory(uploadId).resolve(INVALID_RECORDS_FILE);
	}

	public Path previewPagesDirectory(String uploadId) {
		return previewDirectory(uploadId).resolve(PREVIEW_PAGES_DIR);
	}

	public Path successPagesDirectory(String uploadId) {
		return resultsDirectory(uploadId).resolve(SUCCESS_PAGES_DIR);
	}

	public Path failedPagesDirectory(String uploadId) {
		return resultsDirectory(uploadId).resolve(FAILED_PAGES_DIR);
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

	public Path sessionDirectory(String uploadId) {
		return rootDirectory.resolve(uploadId).normalize();
	}

	private CsvUploadSession loadSession(String uploadId) {
		Path metadataPath = metadataPath(uploadId);
		if (!Files.exists(metadataPath)) {
			throw new IllegalArgumentException("Upload session not found.");
		}

		try {
			return objectMapper.readValue(metadataPath.toFile(), CsvUploadSession.class);
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to load upload session metadata.", exception);
		}
	}

	private Path metadataPath(String uploadId) {
		return sessionDirectory(uploadId).resolve(METADATA_FILE);
	}

	private Path previewDirectory(String uploadId) {
		return sessionDirectory(uploadId).resolve(PREVIEW_DIR);
	}

	private Path resultsDirectory(String uploadId) {
		return sessionDirectory(uploadId).resolve(RESULTS_DIR);
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
					throw new IllegalStateException("Unable to clear upload session artifacts.", exception);
				}
			});
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to clear upload session artifacts.", exception);
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
}
