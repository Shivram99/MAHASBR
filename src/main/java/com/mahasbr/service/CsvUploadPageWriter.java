package com.mahasbr.service;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CsvUploadPageWriter<T> implements Closeable {

	private final ObjectMapper objectMapper;
	private final Path outputDirectory;
	private final int pageSize;
	private final String filePrefix;
	private final List<T> currentPage = new ArrayList<>();
	private int pageCounter;
	private int totalRecords;

	public CsvUploadPageWriter(ObjectMapper objectMapper, Path outputDirectory, int pageSize, String filePrefix)
			throws IOException {
		this.objectMapper = objectMapper;
		this.outputDirectory = outputDirectory;
		this.pageSize = pageSize;
		this.filePrefix = filePrefix;
		Files.createDirectories(outputDirectory);
	}

	public void append(T record) throws IOException {
		currentPage.add(record);
		totalRecords++;
		if (currentPage.size() >= pageSize) {
			flushCurrentPage();
		}
	}

	public int getPageCounter() {
		return pageCounter;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	@Override
	public void close() throws IOException {
		flushCurrentPage();
	}

	private void flushCurrentPage() throws IOException {
		if (currentPage.isEmpty()) {
			return;
		}

		pageCounter++;
		Path pagePath = outputDirectory.resolve(filePrefix + "-" + String.format("%06d", pageCounter) + ".json");
		objectMapper.writeValue(pagePath.toFile(), currentPage);
		currentPage.clear();
	}
}
