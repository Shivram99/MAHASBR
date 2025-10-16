package com.mahasbr.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.ConcernRegistryDetailsPageEntity;
import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.DuplicateRegistryDetailsPageEntity;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.model.PostLoginDashboardRequestModel;
import com.mahasbr.response.ExcelFileUpaloadResult;
import com.mahasbr.service.ConcernRegistryDetailsPageService;
import com.mahasbr.service.DistrictMasterService;
import com.mahasbr.service.DuplicateRegistryDetailsPageService;
import com.mahasbr.service.FileStorageService;
import com.mahasbr.service.MstRegistryDetailsPageService;

@RestController
@RequestMapping("/api/auth")
public class RegistryCSVUploadController {

	@Autowired
	MstRegistryDetailsPageService mstRegistryDetailsPageService;

	@Autowired
	ConcernRegistryDetailsPageService concernRegistryDetailsPageService;

	@Autowired
	DuplicateRegistryDetailsPageService duplicateRegistryDetailsPageService;

	@Autowired
	private FileStorageService service;
	
	@Autowired
	private DistrictMasterService districtservice;

	public RegistryCSVUploadController(FileStorageService service) {
		this.service = service;
	}

	@GetMapping("/registoryData")
	public ResponseEntity<Page<MstRegistryDetailsPageEntity>> getMasterRegistoryDetails(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size,
			@RequestParam(defaultValue = "siNo") String sortBy,
			Authentication authentication){
		
		 // Get logged-in username
	    String username = authentication.getName();

//	    // Get roles
	    Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
	    System.out.println("username" +username);
	    for (GrantedAuthority role : roles) {
	        System.out.println("Role: " + role.getAuthority());
	    }
	   
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
		details.getContent().forEach(entity -> {
		    System.out.println("SI.No: " + entity.getSiNo());
		    // print other fields...
		});
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

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
		if (files == null || files.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("error", "No files provided"));
		}

		List<ExcelFileUpaloadResult> results = new ArrayList<>();
		for (MultipartFile file : files) {
			try {
				mstRegistryDetailsPageService.uploadRegiteryCSVFileForBRNGeneration(file);
			} catch (Exception e) {
				results.add(ExcelFileUpaloadResult.failure(file.getOriginalFilename(), e.getMessage()));
			}
		}
		return ResponseEntity.ok(Map.of("files", results));
	}

	
	@GetMapping("/districts")
	public ResponseEntity<List<DistrictMaster>> getAll() {
//		return ResponseEntity.ok(districtservice.findByIsActiveTrue());
		return ResponseEntity.ok(districtservice.findByIsActiveTrueBasedOnlogin());
	}
}
