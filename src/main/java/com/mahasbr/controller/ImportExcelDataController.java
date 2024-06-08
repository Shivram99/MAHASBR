package com.mahasbr.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
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
@RequestMapping("/user")
public class ImportExcelDataController {
	@Autowired
	DetailsPageRepository detailsPageRepository;
	private static final Logger logger = LoggerFactory.getLogger(DetailsPageController.class);
	private static final String CSV_FILE_LOCATION = "D:\\SBR PROJECT\\Docs\\12_AKOLA_7Act Sample.xlsx";

	@GetMapping("/importExcelSheet")
	public @ResponseBody void readCSV() {
		
		
		String fileArr[]= {"D:\\SBR PROJECT\\Docs\\12_THANE_7Act_Sample_12_13.xlsx"};
		
		
		for(String path:fileArr) {
			Workbook workbook = null;
			try {
				workbook = WorkbookFactory.create(new File(path));

				// Retrieving the number of sheets in the Workbook
				logger.info("Number of sheets: ", workbook.getNumberOfSheets());
				// Print all sheets name

				int index = 0;
				for (Sheet sheet : workbook) {
					DataFormatter dataFormatter = new DataFormatter();
					// loop through all rows and columns and create Course object
					for (Row row : sheet) {
						if (index>9) {
							DetailsPage details = new DetailsPage();
							details.setNameOfEstateOwner(dataFormatter.formatCellValue(row.getCell(1)));
							details.setHouseNo(dataFormatter.formatCellValue(row.getCell(2)));
							details.setStreetName(dataFormatter.formatCellValue(row.getCell(3)));
							details.setLocality(dataFormatter.formatCellValue(row.getCell(4)));
							details.setTownVillage(dataFormatter.formatCellValue(row.getCell(5)));
							details.setTaluka(dataFormatter.formatCellValue(row.getCell(6)));
							details.setDistrict(dataFormatter.formatCellValue(row.getCell(7)));
							
							if(row.getCell(8)!=null && !(dataFormatter.formatCellValue(row.getCell(8)) instanceof String))
							details.setPincode(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(8))));
							
							details.setSector(dataFormatter.formatCellValue(row.getCell(9)));
							details.setActRegNo(dataFormatter.formatCellValue(row.getCell(10)));
							details.setNameofAuth(dataFormatter.formatCellValue(row.getCell(11)));
							details.setNameofAct(dataFormatter.formatCellValue(row.getCell(12)));
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
}
