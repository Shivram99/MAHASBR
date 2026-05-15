package com.mahasbr.util;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import com.mahasbr.dto.RegisteredEstablishmentExportDto;

@Component
public class ExcelExportUtil {

	private static final DateTimeFormatter REPORT_DATE_TIME_FORMAT =
			DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	private static final String[] HEADERS = {
			"Sr.No",
			"Name of Establishment / Owner",
			"House No",
			"Street Name",
			"Locality",
			"Pin Code",
			"Telephone / Mobile Number",
			"Email Address",
			"PAN",
			"TAN",
			"Head Office House No",
			"Head Office Street Name",
			"Head Office Locality",
			"Head Office Pin Code",
			"Head Office Telephone / Mobile Number",
			"Head Office Email Address",
			"Head Office PAN",
			"Head Office TAN",
			"Description of Major Activity",
			"NIC 2008 Activity Code",
			"NIC 2008 Activity Code Description",
			"Year of Start of Operation",
			"Ownership Code",
			"Total Number of Persons Working",
			"Act / Authority Registration Numbers",
			"Remarks",
			"Location Code",
			"Registration Status",
			"BRN",
			"Town / Village",
			"Taluka",
			"District",
			"Sector",
			"Ward Number",
			"Authority Name",
			"Act Name",
			"Date of Registration",
			"Date of Deregistration / Expiry",
			"GST Number",
			"HSN Code"
	};

	private static final int[] COLUMN_WIDTHS = {
			4500, 12000, 5500, 9000, 9000, 4500, 7000, 10000, 5000, 5000,
			6500, 9000, 9000, 5000, 7500, 10000, 5500, 5500, 10000, 6500,
			8500, 6500, 5000, 6500, 9500, 9000, 6500, 5500, 7000, 7000,
			6500, 6500, 5000, 5000, 9000, 9000, 6500, 8000, 6500, 5000
	};

	private static final int LAST_COLUMN_INDEX = HEADERS.length - 1;

	public long writeRegisteredEstablishmentsWorkbook(
			OutputStream outputStream,
			LocalDateTime generatedAt,
			Map<String, String> filterSummary,
			RowBatchWriter rowBatchWriter) throws IOException {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook(200)) {
			workbook.setCompressTempFiles(true);

			Sheet sheet = workbook.createSheet("Registered Establishments");
			configureSheet(sheet);
			ExcelStyles styles = createStyles(workbook);

			int rowIndex = writeReportHeader(sheet, generatedAt, filterSummary, styles);
			int headerRowIndex = rowIndex;
			rowIndex = writeTableHeader(sheet, rowIndex, styles);
			sheet.createFreezePane(0, headerRowIndex + 1);

			int[] rowIndexHolder = new int[] { rowIndex };
			long exportedRows = rowBatchWriter.write(rows -> appendRows(sheet, rowIndexHolder, rows, styles));
			if (exportedRows == 0) {
				addNoDataRow(sheet, rowIndexHolder, styles);
			}

			workbook.write(outputStream);
			outputStream.flush();
			workbook.dispose();
			return exportedRows;
		}
	}

	private void configureSheet(Sheet sheet) {
		for (int columnIndex = 0; columnIndex < COLUMN_WIDTHS.length; columnIndex++) {
			sheet.setColumnWidth(columnIndex, COLUMN_WIDTHS[columnIndex]);
		}
	}

	private ExcelStyles createStyles(SXSSFWorkbook workbook) {
		CellStyle titleStyle = workbook.createCellStyle();
		Font titleFont = workbook.createFont();
		titleFont.setBold(true);
		titleFont.setFontHeightInPoints((short) 14);
		titleStyle.setFont(titleFont);
		titleStyle.setAlignment(HorizontalAlignment.CENTER);
		titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		CellStyle metadataStyle = workbook.createCellStyle();
		Font metadataFont = workbook.createFont();
		metadataFont.setFontHeightInPoints((short) 10);
		metadataStyle.setFont(metadataFont);

		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setColor(IndexedColors.WHITE.getIndex());
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setWrapText(true);
		applyBorders(headerStyle);

		CellStyle bodyStyle = workbook.createCellStyle();
		bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		bodyStyle.setWrapText(true);
		applyBorders(bodyStyle);

		CellStyle numberStyle = workbook.createCellStyle();
		numberStyle.cloneStyleFrom(bodyStyle);
		numberStyle.setAlignment(HorizontalAlignment.CENTER);

		CellStyle noDataStyle = workbook.createCellStyle();
		noDataStyle.cloneStyleFrom(bodyStyle);
		noDataStyle.setAlignment(HorizontalAlignment.CENTER);

		return new ExcelStyles(titleStyle, metadataStyle, headerStyle, bodyStyle, numberStyle, noDataStyle);
	}

	private void applyBorders(CellStyle style) {
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
	}

	private int writeReportHeader(Sheet sheet, LocalDateTime generatedAt, Map<String, String> filterSummary,
			ExcelStyles styles) {
		int rowIndex = 0;

		Row titleRow = sheet.createRow(rowIndex++);
		titleRow.setHeightInPoints(22f);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue("Registered Establishments Report");
		titleCell.setCellStyle(styles.titleStyle());
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, LAST_COLUMN_INDEX));

		Row generatedOnRow = sheet.createRow(rowIndex++);
		Cell generatedOnCell = generatedOnRow.createCell(0);
		generatedOnCell.setCellValue("Generated On: " + REPORT_DATE_TIME_FORMAT.format(generatedAt));
		generatedOnCell.setCellStyle(styles.metadataStyle());
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, LAST_COLUMN_INDEX));

		for (Map.Entry<String, String> entry : filterSummary.entrySet()) {
			Row filterRow = sheet.createRow(rowIndex++);
			Cell keyCell = filterRow.createCell(0);
			keyCell.setCellValue(entry.getKey());
			keyCell.setCellStyle(styles.metadataStyle());

			Cell valueCell = filterRow.createCell(1);
			valueCell.setCellValue(entry.getValue());
			valueCell.setCellStyle(styles.metadataStyle());
			sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 1, LAST_COLUMN_INDEX));
		}

		return rowIndex + 1;
	}

	private int writeTableHeader(Sheet sheet, int rowIndex, ExcelStyles styles) {
		Row headerRow = sheet.createRow(rowIndex++);

		for (int columnIndex = 0; columnIndex < HEADERS.length; columnIndex++) {
			Cell cell = headerRow.createCell(columnIndex);
			cell.setCellValue(HEADERS[columnIndex]);
			cell.setCellStyle(styles.headerStyle());
		}

		return rowIndex;
	}

	private void appendRows(Sheet sheet, int[] rowIndexHolder, List<RegisteredEstablishmentExportDto> rows,
			ExcelStyles styles) {
		for (RegisteredEstablishmentExportDto row : rows) {
			Row sheetRow = sheet.createRow(rowIndexHolder[0]++);
			setCell(sheetRow, 0, row.srNo(), styles.numberStyle());
			setCell(sheetRow, 1, row.establishmentName(), styles.bodyStyle());
			setCell(sheetRow, 2, row.houseNo(), styles.bodyStyle());
			setCell(sheetRow, 3, row.streetName(), styles.bodyStyle());
			setCell(sheetRow, 4, row.locality(), styles.bodyStyle());
			setCell(sheetRow, 5, row.pinCode(), styles.bodyStyle());
			setCell(sheetRow, 6, row.telephoneNumber(), styles.bodyStyle());
			setCell(sheetRow, 7, row.emailAddress(), styles.bodyStyle());
			setCell(sheetRow, 8, row.panNumber(), styles.bodyStyle());
			setCell(sheetRow, 9, row.tanNumber(), styles.bodyStyle());
			setCell(sheetRow, 10, row.headOfficeHouseNo(), styles.bodyStyle());
			setCell(sheetRow, 11, row.headOfficeStreetName(), styles.bodyStyle());
			setCell(sheetRow, 12, row.headOfficeLocality(), styles.bodyStyle());
			setCell(sheetRow, 13, row.headOfficePinCode(), styles.bodyStyle());
			setCell(sheetRow, 14, row.headOfficeTelephoneNumber(), styles.bodyStyle());
			setCell(sheetRow, 15, row.headOfficeEmailAddress(), styles.bodyStyle());
			setCell(sheetRow, 16, row.headOfficePanNumber(), styles.bodyStyle());
			setCell(sheetRow, 17, row.headOfficeTanNumber(), styles.bodyStyle());
			setCell(sheetRow, 18, row.majorActivityDescription(), styles.bodyStyle());
			setCell(sheetRow, 19, row.nic2008ActivityCode(), styles.bodyStyle());
			setCell(sheetRow, 20, row.nic2008ActivityCodeDescription(), styles.bodyStyle());
			setCell(sheetRow, 21, row.yearOfStartOfOperation(), styles.bodyStyle());
			setCell(sheetRow, 22, row.ownershipCode(), styles.bodyStyle());
			setCell(sheetRow, 23, row.totalPersonsWorking(), styles.bodyStyle());
			setCell(sheetRow, 24, row.actAuthorityRegistrationNumbers(), styles.bodyStyle());
			setCell(sheetRow, 25, row.remarks(), styles.bodyStyle());
			setCell(sheetRow, 26, row.locationCode(), styles.bodyStyle());
			setCell(sheetRow, 27, row.registrationStatus(), styles.bodyStyle());
			setCell(sheetRow, 28, row.brn(), styles.bodyStyle());
			setCell(sheetRow, 29, row.townVillage(), styles.bodyStyle());
			setCell(sheetRow, 30, row.taluka(), styles.bodyStyle());
			setCell(sheetRow, 31, row.district(), styles.bodyStyle());
			setCell(sheetRow, 32, row.sector(), styles.bodyStyle());
			setCell(sheetRow, 33, row.wardNumber(), styles.bodyStyle());
			setCell(sheetRow, 34, row.authorityName(), styles.bodyStyle());
			setCell(sheetRow, 35, row.actName(), styles.bodyStyle());
			setCell(sheetRow, 36, row.registrationDate(), styles.bodyStyle());
			setCell(sheetRow, 37, row.deregistrationExpiryDate(), styles.bodyStyle());
			setCell(sheetRow, 38, row.gstNumber(), styles.bodyStyle());
			setCell(sheetRow, 39, row.hsnCode(), styles.bodyStyle());
		}
	}

	private void addNoDataRow(Sheet sheet, int[] rowIndexHolder, ExcelStyles styles) {
		Row row = sheet.createRow(rowIndexHolder[0]);
		Cell cell = row.createCell(0);
		cell.setCellValue("No records found for the selected filters.");
		cell.setCellStyle(styles.noDataStyle());
		sheet.addMergedRegion(new CellRangeAddress(rowIndexHolder[0], rowIndexHolder[0], 0, LAST_COLUMN_INDEX));
	}

	private void setCell(Row row, int columnIndex, Object value, CellStyle style) {
		Cell cell = row.createCell(columnIndex);
		cell.setCellValue(toDisplayValue(value));
		cell.setCellStyle(style);
	}

	private String toDisplayValue(Object value) {
		if (value == null) {
			return "-";
		}

		String normalizedValue = value.toString().trim();
		return normalizedValue.isEmpty() ? "-" : normalizedValue;
	}

	private record ExcelStyles(
			CellStyle titleStyle,
			CellStyle metadataStyle,
			CellStyle headerStyle,
			CellStyle bodyStyle,
			CellStyle numberStyle,
			CellStyle noDataStyle) {
	}

	@FunctionalInterface
	public interface RowBatchWriter {
		long write(RowConsumer consumer) throws IOException;
	}

	@FunctionalInterface
	public interface RowConsumer {
		void accept(List<RegisteredEstablishmentExportDto> rows) throws IOException;
	}
}
