package com.mahasbr.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.model.SearchBrnDto;
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

	private final DistrictMasterService districtservice;

	private final CommonService commonService;

	private final MstRegistryDetailsPageService mstRegistryDetailsPageService;
	
	private final TalukaMasterService talukaMasterService;
	
	private final BrnGeneratorFactory generatorFactory;

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

	 @PostMapping("/register")
	    public String register() {
	        String brn = generatorFactory.getGenerator().generateBrn("27");
	        return brn;
	    }
}
