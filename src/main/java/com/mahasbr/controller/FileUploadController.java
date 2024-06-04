package com.mahasbr.controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;

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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.DetailsPage;
import com.mahasbr.repository.DetailsPageRepository;

@RestController
@RequestMapping("/admin/api/test")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private DetailsPageRepository detailsPageRepository;

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public  void uploadFile(@RequestParam("file") MultipartFile file) {
        Workbook workbook = null;
        try {
            if (file.getOriginalFilename().endsWith(".csv")) {
                processCSVFile(file);
            } else if (file.getOriginalFilename().endsWith(".xlsx")) {
                workbook = WorkbookFactory.create(file.getInputStream());
                processExcelFile(workbook);
            } else {
                throw new IllegalArgumentException("Unsupported file type");
            }
        } catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void processCSVFile(MultipartFile file) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {
                if (index != 0) {
                    String[] values = line.split(",");
                    DetailsPage details = new DetailsPage();
                    details.setNameOfEstateOwner(values[1]);
                    details.setHouseNo(values[2]);
                    details.setStreetName(values[3]);
                    details.setLocality(values[4]);
                    details.setPincode(Integer.parseInt(values[5]));
                    details.setTelMobNo(Long.parseLong(values[6]));
                    details.setEmailAddress(values[7]);
                    details.setPanNo(values[8]);
                    details.setTanNo(values[9]);
                    details.setHohouseNo(values[10]);
                    details.setHoStreetName(values[11]);
                    details.setHoLocality(values[12]);
                    details.setHoPincode(Integer.parseInt(values[13]));
                    details.setHoTelMobNo(Long.parseLong(values[14]));
                    details.setHoEmailAddress(values[15]);
                    details.setHoPanNo(values[16]);
                    details.setHoTanNo(values[17]);
                    details.setMajarActOfEst(values[18]);
                    details.setNicActCode(Integer.parseInt(values[19]));

                    LocalDate localDate = LocalDate.parse(values[20]);
                    details.setOpCurStartDate(localDate);
                    details.setOwnCode(Integer.parseInt(values[21]));
                    details.setNoOfWorkers(Integer.parseInt(values[22]));
                    details.setActRegNo(values[23]);
                    details.setRemarks(values[24]);
                    details.setLoccode(values[25]);
                    details.setBusRegNo(values[26]);
                    details.setAadharNo(Long.parseLong(values[27]));
                    details.setRegstatus(values[28]);
                    details.setTownVillage(values[29]);
                    details.setTaluka(values[30]);
                    details.setDistrict(values[31]);
                    details.setSector(values[32]);
                    details.setNameofAuth(values[33]);
                    details.setNameofAct(values[34]);
                    details.setDateOfReg(values[35]);
                    details.setDateOfExpiry(values[36]);

                    detailsPageRepository.save(details);
                }
                index++;
            }
        }
    }

    private void processExcelFile(Workbook workbook) {
        DataFormatter dataFormatter = new DataFormatter();
        int index = 0;
        for (Sheet sheet : workbook) {
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

                    LocalDate localDate = LocalDate.parse(dataFormatter.formatCellValue(row.getCell(20)));
                    details.setOpCurStartDate(localDate);
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
                    details.setNameofAct(dataFormatter.formatCellValue(row.getCell(34)));
                    details.setDateOfReg(dataFormatter.formatCellValue(row.getCell(35)));
                    details.setDateOfExpiry(dataFormatter.formatCellValue(row.getCell(36)));

                    detailsPageRepository.save(details);
                }
                index++;
            }
        }
    }
}
