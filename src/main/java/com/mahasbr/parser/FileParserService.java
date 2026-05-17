package com.mahasbr.parser;

import java.io.IOException;
import java.nio.file.Path;

public interface FileParserService {
	boolean supports(String fileName);

	void parse(Path filePath, CsvUploadRowHandler rowHandler) throws IOException;
}
