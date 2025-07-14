package com.mahasbr.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.ConcernRegistryDetailsPageEntity;
import com.mahasbr.entity.District;
import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.DuplicateRegistryDetailsPageEntity;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.entity.RegionEntity;
import com.mahasbr.entity.User;
import com.mahasbr.model.BRNGenerationRecordCount;
import com.mahasbr.model.PostLoginDashboardRequestModel;
import com.mahasbr.service.ConcernRegistryDetailsPageService;
import com.mahasbr.service.DuplicateRegistryDetailsPageService;
import com.mahasbr.service.MstRegistryDetailsPageService;
import com.mahasbr.util.CommonMethod;

@RestController
@RequestMapping("/api/auth")
public class RegistryCSVUploadController {

	@Autowired
	MstRegistryDetailsPageService mstRegistryDetailsPageService;

	@Autowired
	ConcernRegistryDetailsPageService concernRegistryDetailsPageService;

	@Autowired
	DuplicateRegistryDetailsPageService duplicateRegistryDetailsPageService;

//	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadExcelFile(@RequestPart("file") MultipartFile file) {
		BRNGenerationRecordCount bRNGenerationRecordCount = null;
		// Check if the file is empty
		if (file.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload.");
		}

		// Get the original filename
		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file name.");
		}

		// Check file type
		if (!(originalFilename.endsWith(".csv") || originalFilename.endsWith(".xlsx"))) {
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
					.body("Unsupported file type. Please upload a CSV or Excel (.xlsx) file.");
		}

		try {
			// Process the file based on its type
			if (originalFilename.endsWith(".xlsx")) {
				bRNGenerationRecordCount = mstRegistryDetailsPageService.uploadRegiteryCSVFileForBRNGeneration(file);
				System.out.println("bRNGenerationRecordCount :" + bRNGenerationRecordCount);
			}

		} catch (Exception e) {
			// Log the error and return an error response
			// logger.error("Error processing file: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to upload file: " + e.getMessage());
		}

		return ResponseEntity.ok().body(bRNGenerationRecordCount);
	}

	@GetMapping("/registoryData")
	public ResponseEntity<Page<MstRegistryDetailsPageEntity>> getMasterRegistoryDetails(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "siNo") String sortBy) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
		Page<MstRegistryDetailsPageEntity> registryDetailsPage = mstRegistryDetailsPageService
				.getAllRegistoryDetails(pageable);
		return ResponseEntity.ok(registryDetailsPage);
	}

	

	@GetMapping("/registoryDuplicateData")
	public ResponseEntity<Page<DuplicateRegistryDetailsPageEntity>> getDuplicateRegistryDetails(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "siNo") String sortBy) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
		Page<DuplicateRegistryDetailsPageEntity> duplcateRegistryDetailsPage = duplicateRegistryDetailsPageService
				.getAllDuplicateRegistryDetails(pageable);
		return ResponseEntity.ok(duplcateRegistryDetailsPage);
	}

	@GetMapping("/registoryConcernData")
	public ResponseEntity<Page<ConcernRegistryDetailsPageEntity>> getConcernRegistryDetails(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "siNo") String sortBy) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
		Page<ConcernRegistryDetailsPageEntity> concernRegistryDetailsPage = concernRegistryDetailsPageService
				.getAllConcernRegistryDetails(pageable);
		return ResponseEntity.ok(concernRegistryDetailsPage);
	}

	@GetMapping("/brn-details/{brn}")
	public ResponseEntity<Page<MstRegistryDetailsPageEntity>> getBRNDetails(@PathVariable String brn) {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("siNo"));
		Page<MstRegistryDetailsPageEntity> details = mstRegistryDetailsPageService.getBRNData(brn, pageable);
		return ResponseEntity.ok(details);
	}

	@PostMapping(value = "/getPostLoginDashboardData", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Page<MstRegistryDetailsPageEntity>> getPostLoginDashboardData(
			@RequestBody PostLoginDashboardRequestModel request) {

		// Extract parameters from the request
		int page = request.getPage();
		int size = request.getSize();
		String sortBy = request.getSortBy();
		List<Long> selectedDistrictIds = request.getSelectedDistrictIds();
		List<Long> selectedTalukaIds = request.getSelectedTalukaIds();
		String registerDateFrom = request.getFilters().getRegisterDateFrom();
		String registerDateTo = request.getFilters().getRegisterDateTo();
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
		// Implement the service call to get the data based on the parameters
		Page<MstRegistryDetailsPageEntity> response = mstRegistryDetailsPageService.getPostLoginDashboardData(pageable,
				selectedDistrictIds, selectedTalukaIds, registerDateFrom, registerDateTo);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/download")
	public ResponseEntity<Resource> downloadFile(@RequestParam String fileName) {
		try {
			// Assuming files are stored in a directory named "files"
			Path filePath = Paths.get("src/main/resources/static/files/" + fileName);
			Resource resource = new UrlResource(filePath.toUri());

			if (!resource.exists()) {
				throw new RuntimeException("File not found " + fileName);
			}

			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);

		} catch (Exception e) {
			throw new RuntimeException("Error occurred while downloading file " + fileName, e);
		}
	}
}
