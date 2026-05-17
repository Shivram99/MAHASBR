package com.mahasbr.parser;

import java.io.IOException;
import java.util.Map;

@FunctionalInterface
public interface CsvUploadRowHandler {
	void handle(int rowNumber, Map<String, String> rowData) throws IOException;
}
