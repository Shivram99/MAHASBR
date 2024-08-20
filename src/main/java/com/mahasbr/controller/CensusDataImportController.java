package com.mahasbr.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import com.mahasbr.entity.CensusEntity;
import com.mahasbr.repository.CensusEntityRepository;

@RestController
@RequestMapping("/user")
public class CensusDataImportController {

	@Autowired
	CensusEntityRepository censusEntityRepository;

	private static final Logger logger = LoggerFactory.getLogger(DetailsPageController.class);
//private static final String CSV_FILE_LOCATION = "C:\\Users\\Mahait\\Downloads\\Rdir_2011_27_MAHARASHTRA.xls";

	@GetMapping("/importCensusExcelSheet")
	public @ResponseBody void importCensusExcelSheet() {

		String fileArr[] = { "C:\\Users\\Mahait\\Downloads\\Rdir_2011_27_MAHARASHTRA.xls" };

		for (String path : fileArr) {
			Workbook workbook = null;
			try {
				workbook = WorkbookFactory.create(new File(path));

				// Retrieving the number of sheets in the Workbook
				logger.info("Number of sheets: ", workbook.getNumberOfSheets());
				// Print all sheets name
				List<CensusEntity> lstCensusEntity = new ArrayList<>();
				int index = 0;
				for (Sheet sheet : workbook) {
					DataFormatter dataFormatter = new DataFormatter();
					// loop through all rows and columns and create Course object
					for (Row row : sheet) {
						if (index > 1) {

							CensusEntity censusEntity = new CensusEntity();

							censusEntity.setCensusStateCode(dataFormatter.formatCellValue(row.getCell(0)));
							censusEntity.setCensusStateName(dataFormatter.formatCellValue(row.getCell(1)));
							censusEntity.setCensusDistrictCode(dataFormatter.formatCellValue(row.getCell(2)));
							censusEntity.setCensusDistrictName(dataFormatter.formatCellValue(row.getCell(3)));
							censusEntity.setCensusTahsilCode(dataFormatter.formatCellValue(row.getCell(4)));
							censusEntity.setCensusTahsilName(dataFormatter.formatCellValue(row.getCell(5)));

							censusEntity.setCensusVillageCode(dataFormatter.formatCellValue(row.getCell(6)));
							censusEntity.setCensusVillageName(dataFormatter.formatCellValue(row.getCell(7)));

							lstCensusEntity.add(censusEntity);

						}
						index++;

					}

				}

				censusEntityRepository.saveAll(lstCensusEntity);

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
