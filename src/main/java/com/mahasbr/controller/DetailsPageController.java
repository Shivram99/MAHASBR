package com.mahasbr.controller;

import java.io.File;
import java.io.IOException;
import java.time.Year;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.DetailsPage;
import com.mahasbr.repository.DetailsPageRepository;

@RestController
@RequestMapping("/api/auth")
public class DetailsPageController {
	@Autowired
	DetailsPageRepository detailsPageRepository;
	private static final Logger logger = LoggerFactory.getLogger(DetailsPageController.class);
	private static final String CSV_FILE_LOCATION = "C:\\Users\\Dipali.sonawane\\Desktop\\Book1.xlsx";

	@GetMapping("/detailspage")
	public @ResponseBody void readCSV() {
		Workbook workbook = null;
		try {
			workbook = WorkbookFactory.create(new File(CSV_FILE_LOCATION));

			// Retrieving the number of sheets in the Workbook
			logger.info("Number of sheets: ", workbook.getNumberOfSheets());
			// Print all sheets name

			int index = 0;
			for (Sheet sheet : workbook) {
				DataFormatter dataFormatter = new DataFormatter();
				// loop through all rows and columns and create Course object
				for (Row row : sheet) {
					if (index != 0) {
						DetailsPage details = new DetailsPage();
						details.setNameOfEstateOwner(dataFormatter.formatCellValue(row.getCell(1)));
						details.setHouseNo(dataFormatter.formatCellValue(row.getCell(2)));
						details.setStreetName(dataFormatter.formatCellValue(row.getCell(3)));
						details.setLocality(dataFormatter.formatCellValue(row.getCell(4)));
						details.setPincode(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(5))));
						details.setTelMobNo(Long.parseLong(dataFormatter.formatCellValue(row.getCell(6))));
						details.setEmailAddress(dataFormatter.formatCellValue(row.getCell(7)));
						details.setPanNo(dataFormatter.formatCellValue(row.getCell(8)));
						details.setTanNo(dataFormatter.formatCellValue(row.getCell(9)));
						details.setHohouseNo(dataFormatter.formatCellValue(row.getCell(10)));
						details.setHoStreetName(dataFormatter.formatCellValue(row.getCell(11)));
						details.setHoLocality(dataFormatter.formatCellValue(row.getCell(12)));
						details.setHoPincode(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(13))));
						details.setHoTelMobNo(Long.parseLong(dataFormatter.formatCellValue(row.getCell(14))));
						details.setHoEmailAddress(dataFormatter.formatCellValue(row.getCell(15)));
						details.setHoPanNo(dataFormatter.formatCellValue(row.getCell(16)));
						details.setHoTanNo(dataFormatter.formatCellValue(row.getCell(17)));
						details.setMajarActOfEst(dataFormatter.formatCellValue(row.getCell(18)));
						details.setNicActCode(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(19))));
						details.setOpCurStartDate(Year.parse(dataFormatter.formatCellValue(row.getCell(20))));
						details.setOwnCode(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(21))));
						details.setNoOfWorkers(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(22))));
						details.setActRegNo(dataFormatter.formatCellValue(row.getCell(23)));
						details.setRemarks(dataFormatter.formatCellValue(row.getCell(24)));
						details.setLoccode(dataFormatter.formatCellValue(row.getCell(25)));
						details.setBusRegNo(dataFormatter.formatCellValue(row.getCell(26)));
						details.setAadharNo(Long.parseLong(dataFormatter.formatCellValue(row.getCell(27))));
						details.setRegstatus(dataFormatter.formatCellValue(row.getCell(28)));
						details.setTownVillage(dataFormatter.formatCellValue(row.getCell(29)));
						details.setTaluka(dataFormatter.formatCellValue(row.getCell(30)));
						details.setDistrict(dataFormatter.formatCellValue(row.getCell(31)));
						details.setSector(dataFormatter.formatCellValue(row.getCell(32)));
						details.setNameofAuth(dataFormatter.formatCellValue(row.getCell(33)));
						details.setNameofAct(dataFormatter.formatCellValue(row.getCell(134)));
						details.setDateOfReg(dataFormatter.formatCellValue(row.getCell(35)));
						details.setDateOfExpiry(dataFormatter.formatCellValue(row.getCell(36)));

						detailsPageRepository.save(details);
					}

					index++;
				}
			}

		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (workbook != null)
					workbook.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

	}
}
