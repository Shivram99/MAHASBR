package com.mahasbr.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.MasterNicDetails;
import com.mahasbr.repository.MasterNicDetailsRepository;
import com.mahasbr.util.StringUtils;

@RestController
@RequestMapping("/api/auth")
public class NICMatserDetailsController {
	 
	@Autowired
	MasterNicDetailsRepository masterNicDetailsRepository;
	
	@GetMapping("/nic")
	public List<MasterNicDetails> readExcelData() throws IOException {
		String filePath = "C:\\Users\\jitendra.ghasle\\Downloads\\NIC_2008_MST (2).xlsx";
		StringUtils stringUtils = new StringUtils();
	        List<MasterNicDetails> nicDetailsList = new ArrayList<>();
	        FileInputStream fileInputStream = new FileInputStream(filePath);
	        Workbook workbook = new XSSFWorkbook(fileInputStream);
	        Sheet sheet = workbook.getSheetAt(0);

	        for (int i = 1; i <= sheet.getLastRowNum(); i++) {  // Skipping the header row
	            Row row = sheet.getRow(i);
	            MasterNicDetails nicDetails = new MasterNicDetails();
	            
	            nicDetails.setNicSectionCode(stringUtils.safeUpperCase(row.getCell(0).getStringCellValue()));
	            nicDetails.setNicSectionName(stringUtils.safeUpperCase(row.getCell(1).getStringCellValue()));
	            nicDetails.setNicDivisionCode(stringUtils.safeUpperCase(row.getCell(2).getStringCellValue()));
	            nicDetails.setNicDivisionName(stringUtils.safeUpperCase(row.getCell(3).getStringCellValue()));
	            nicDetails.setNicGroupCode(stringUtils.safeUpperCase(row.getCell(4).getStringCellValue()));
	            nicDetails.setNicGroupName(stringUtils.safeUpperCase(row.getCell(5).getStringCellValue()));
	            nicDetails.setNicClassCode(stringUtils.safeUpperCase(row.getCell(6).getStringCellValue()));
	            nicDetails.setNicClassName(stringUtils.safeUpperCase(row.getCell(7).getStringCellValue()));
	            nicDetails.setNicCode(stringUtils.safeUpperCase(row.getCell(8).getStringCellValue()));
	            nicDetails.setNicName(stringUtils.safeUpperCase(row.getCell(9).getStringCellValue()));
	            nicDetails.setNicStartYear((int) row.getCell(10).getNumericCellValue());

	            nicDetailsList.add(nicDetails);
	        }

	        workbook.close();
	        fileInputStream.close();
	        masterNicDetailsRepository.saveAll(nicDetailsList);
	        System.out.println(nicDetailsList.toString());
	        return nicDetailsList;
	    }
	
	
}
