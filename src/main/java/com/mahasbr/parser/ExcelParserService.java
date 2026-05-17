package com.mahasbr.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

@Service
public class ExcelParserService implements FileParserService {

	private final DataFormatter dataFormatter = new DataFormatter();

	@Override
	public boolean supports(String fileName) {
		if (fileName == null) {
			return false;
		}

		String lower = fileName.toLowerCase();
		return lower.endsWith(".xls") || lower.endsWith(".xlsx");
	}

	@Override
	public void parse(Path filePath, CsvUploadRowHandler rowHandler) throws IOException {
		String lowerFileName = filePath.getFileName().toString().toLowerCase();
		try {
			if (lowerFileName.endsWith(".xlsx")) {
				parseXlsx(filePath, rowHandler);
				return;
			}
			parseXls(filePath, rowHandler);
		} catch (Exception exception) {
			throw new IllegalArgumentException("Invalid Excel file format.", exception);
		}
	}

	private void parseXlsx(Path filePath, CsvUploadRowHandler rowHandler) throws Exception {
		try (OPCPackage opcPackage = OPCPackage.open(filePath.toFile(), PackageAccess.READ)) {
			ReadOnlySharedStringsTable sharedStrings = new ReadOnlySharedStringsTable(opcPackage);
			XSSFReader reader = new XSSFReader(opcPackage);
			StylesTable stylesTable = reader.getStylesTable();
			XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) reader.getSheetsData();

			while (sheets.hasNext()) {
				try (InputStream sheetStream = sheets.next()) {
					XMLReader parser = XMLHelper.newXMLReader();
					StreamingSheetHandler sheetHandler = new StreamingSheetHandler(rowHandler);
					parser.setContentHandler(new XSSFSheetXMLHandler(stylesTable, null, sharedStrings, sheetHandler,
							dataFormatter, false));
					parser.parse(new InputSource(sheetStream));
				}
			}
		}
	}

	private void parseXls(Path filePath, CsvUploadRowHandler rowHandler) throws IOException {
		try (InputStream inputStream = Files.newInputStream(filePath); HSSFWorkbook workbook = new HSSFWorkbook(inputStream)) {
			for (Sheet sheet : workbook) {
				Row headerRow = sheet.getRow(sheet.getFirstRowNum());
				if (headerRow == null) {
					continue;
				}

				List<String> headers = new ArrayList<>();
				int lastCellNum = Math.max(headerRow.getLastCellNum(), 0);
				for (int cellIndex = 0; cellIndex < lastCellNum; cellIndex++) {
					Cell cell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					headers.add(normalizeHeader(dataFormatter.formatCellValue(cell)));
				}

				for (int rowIndex = sheet.getFirstRowNum() + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
					Row row = sheet.getRow(rowIndex);
					if (row == null) {
						continue;
					}

					Map<String, String> rowData = new LinkedHashMap<>();
					boolean hasValue = false;
					for (int cellIndex = 0; cellIndex < headers.size(); cellIndex++) {
						Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						String value = dataFormatter.formatCellValue(cell).trim();
						if (!value.isBlank()) {
							hasValue = true;
						}
						rowData.put(headers.get(cellIndex), value);
					}

					if (hasValue) {
						rowHandler.handle(rowIndex + 1, rowData);
					}
				}
			}
		}
	}

	private String normalizeHeader(String header) {
		if (header == null) {
			return "";
		}

		return header.replace("\uFEFF", "").trim().toUpperCase();
	}

	private final class StreamingSheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

		private final CsvUploadRowHandler rowHandler;
		private final List<String> headers = new ArrayList<>();
		private List<String> currentRowValues = new ArrayList<>();

		private StreamingSheetHandler(CsvUploadRowHandler rowHandler) {
			this.rowHandler = rowHandler;
		}

		@Override
		public void startRow(int rowNum) {
			currentRowValues = new ArrayList<>();
		}

		@Override
		public void endRow(int rowNum) {
			if (rowNum == 0) {
				headers.clear();
				headers.addAll(currentRowValues.stream().map(ExcelParserService.this::normalizeHeader).toList());
				return;
			}

			if (headers.isEmpty()) {
				return;
			}

			Map<String, String> rowData = new LinkedHashMap<>();
			boolean hasValue = false;
			for (int index = 0; index < headers.size(); index++) {
				String value = index < currentRowValues.size() ? currentRowValues.get(index).trim() : "";
				if (!value.isBlank()) {
					hasValue = true;
				}
				rowData.put(headers.get(index), value);
			}

			if (hasValue) {
				try {
					rowHandler.handle(rowNum + 1, rowData);
				} catch (IOException exception) {
					throw new RuntimeException(exception);
				}
			}
		}

		@Override
		public void cell(String cellReference, String formattedValue, org.apache.poi.xssf.usermodel.XSSFComment comment) {
			int columnIndex = cellReference == null ? currentRowValues.size() : new CellReference(cellReference).getCol();
			while (currentRowValues.size() <= columnIndex) {
				currentRowValues.add("");
			}
			currentRowValues.set(columnIndex, formattedValue == null ? "" : formattedValue);
		}

		@Override
		public void headerFooter(String text, boolean isHeader, String tagName) {
			// no-op
		}
	}
}
