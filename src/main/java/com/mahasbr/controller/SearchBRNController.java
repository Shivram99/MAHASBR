package com.mahasbr.controller;

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.dto.CircularResponseDTO;
import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.model.SearchBrnDto;
import com.mahasbr.service.CircularService;
import com.mahasbr.service.CommonService;
import com.mahasbr.service.DistrictMasterService;
import com.mahasbr.service.MstRegistryDetailsPageService;
import com.mahasbr.service.TalukaMasterService;
import com.mahasbr.util.BrnGeneratorFactory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/citizenSearch")
@RequiredArgsConstructor
public class SearchBRNController {
	
	private static final Logger logger = LoggerFactory.getLogger(SearchBRNController.class);

	private final DistrictMasterService districtservice;

	private final CommonService commonService;

	private final MstRegistryDetailsPageService mstRegistryDetailsPageService;
	
	private final TalukaMasterService talukaMasterService;
	
	private final BrnGeneratorFactory generatorFactory;
	
    private final CircularService circularService;

	@GetMapping("/districts")
	public ResponseEntity<List<DistrictMaster>> getAll() {
		return ResponseEntity.ok(districtservice.findByIsActiveTrue());
	}

	@PostMapping("/searchBRN")
	public ResponseEntity<List<MstRegistryDetailsPageEntity>> searchBRN(@Valid @RequestBody SearchBrnDto searchBrnDto) {
		if (StringUtils.isBlank(searchBrnDto.getDistrict())) {
			throw new IllegalArgumentException("District code must not be blank");
		}

		DistrictMaster district = districtservice.findByCensusDistrictCode(searchBrnDto.getDistrict()).orElseThrow(
				() -> new ResourceNotFoundException("District", "CensusDistrictCode", searchBrnDto.getDistrict()));

		String districtName = StringUtils.upperCase(district.getDistrictName());
		String brnNo = StringUtils.upperCase(searchBrnDto.getBrnNo());
		String establishmentOrOwnerName = StringUtils.upperCase(searchBrnDto.getNameOfEstablishmentOrOwner());

		List<MstRegistryDetailsPageEntity> result = mstRegistryDetailsPageService
				.getsearchBRNAndEstablishmentDetails(districtName, brnNo, establishmentOrOwnerName);

		return ResponseEntity.ok(result);
	}

	@PostMapping("/districtTaluka")
	public ResponseEntity<List<TalukaMaster>> getDistrictTaluka(@RequestBody List<String> districtCode) {
		return ResponseEntity.ok(talukaMasterService.findByCensusDistrictCodeInAndIsActiveTrue(districtCode));
	}
	
	@GetMapping("/circulars")
    public ResponseEntity<List<CircularResponseDTO>> getAllCirculars() {
        return ResponseEntity.ok(circularService.getAllCirculars());
    }
	
	@GetMapping("/files/{fileName:.+}")
	public ResponseEntity<Resource> getCircularFile(@PathVariable String fileName) {
		System.out.println(fileName);
	    try {
	        Resource resource = circularService.getCircularFile("circulars/" + fileName);

	        return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_PDF)
	                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
	                .body(resource);

	    } catch (FileNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(null);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(null);
	    }
	}
}
