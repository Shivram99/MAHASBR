package com.mahasbr.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.response.MessageResponse;
import com.mahasbr.service.DistrictMasterService;

@RestController
@RequestMapping("/developer")
public class DistrictMasterController {
	@Autowired
	DistrictMasterService districtMasterService;

	@Autowired
	DistrictMasterRepository districtMasterRepository;

//	@PostMapping("/district")
//	public ResponseEntity<?> postDistrictDetails(@RequestBody DistrictMasterModel districtMasterModel) {
//		DistrictMaster district = districtMasterService.insertDistrictDetail(districtMasterModel);
//		return ResponseEntity.ok(new MessageResponse("Added successfully!", district));
//	}

	@GetMapping
	public @ResponseBody List<DistrictMaster> readCSV() {
		List<DistrictMaster> districts = districtMasterService.readdataCsv();
		return districts;	
	}
}