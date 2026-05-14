package com.mahasbr.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.mahasbr.dto.RegisteredEstablishmentExportDto;

@Component
public class PdfExportUtil {

	private static final DateTimeFormatter REPORT_DATE_TIME_FORMAT =
			DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
	private static final Font META_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
	private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
	private static final Font BODY_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);

	public Document createLandscapeDocument() {
		return new Document(PageSize.A4.rotate(), 24f, 24f, 28f, 28f);
	}

	public void addReportHeader(Document document, LocalDateTime generatedAt, Map<String, String> filterSummary)
			throws DocumentException {
		Paragraph title = new Paragraph("Registered Establishments Report", TITLE_FONT);
		title.setAlignment(Element.ALIGN_CENTER);
		title.setSpacingAfter(8f);
		document.add(title);

		Paragraph generatedOn = new Paragraph(
				"Generated On: " + REPORT_DATE_TIME_FORMAT.format(generatedAt),
				META_FONT);
		generatedOn.setAlignment(Element.ALIGN_RIGHT);
		generatedOn.setSpacingAfter(10f);
		document.add(generatedOn);

		for (Map.Entry<String, String> filterEntry : filterSummary.entrySet()) {
			Paragraph filterLine = new Paragraph(
					filterEntry.getKey() + ": " + filterEntry.getValue(),
					META_FONT);
			filterLine.setSpacingAfter(4f);
			document.add(filterLine);
		}

		document.add(new Paragraph(" "));
	}

	public PdfPTable createRegisteredEstablishmentsTable() throws DocumentException {
		PdfPTable table = new PdfPTable(new float[] { 1.2f, 2.6f, 5.2f, 3.2f, 3.0f, 2.6f });
		table.setWidthPercentage(100);
		table.setSpacingBefore(4f);
		table.setSpacingAfter(8f);

		addHeaderCell(table, "Sr.No");
		addHeaderCell(table, "BRN");
		addHeaderCell(table, "Establishment Name");
		addHeaderCell(table, "City");
		addHeaderCell(table, "District");
		addHeaderCell(table, "Institution Type");
		return table;
	}

	public void appendRows(PdfPTable table, List<RegisteredEstablishmentExportDto> rows) {
		for (RegisteredEstablishmentExportDto row : rows) {
			addBodyCell(table, toDisplayValue(row.srNo()));
			addBodyCell(table, row.brn());
			addBodyCell(table, row.establishmentName());
			addBodyCell(table, row.city());
			addBodyCell(table, row.district());
			addBodyCell(table, row.institutionType());
		}
	}

	public void addNoDataRow(PdfPTable table) {
		PdfPCell cell = new PdfPCell(new Phrase("No records found for the selected filters.", BODY_FONT));
		cell.setColspan(6);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(10f);
		table.addCell(cell);
	}

	private void addHeaderCell(PdfPTable table, String label) {
		PdfPCell cell = new PdfPCell(new Phrase(label, HEADER_FONT));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(7f);
		cell.setBackgroundColor(new BaseColor(31, 78, 121));
		cell.setBorderColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);
	}

	private void addBodyCell(PdfPTable table, String value) {
		PdfPCell cell = new PdfPCell(new Phrase(toDisplayValue(value), BODY_FONT));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(6f);
		cell.setUseAscender(true);
		cell.setUseDescender(true);
		cell.setBorderColor(new BaseColor(210, 210, 210));
		table.addCell(cell);
	}

	private String toDisplayValue(Object value) {
		if (value == null) {
			return "-";
		}

		String normalizedValue = value.toString().trim();
		return normalizedValue.isEmpty() ? "-" : normalizedValue;
	}
}
