package com.mahasbr.service;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

	void processExcelFile(Workbook workbook);

	void processCSVFile(MultipartFile file)throws IOException;


}
