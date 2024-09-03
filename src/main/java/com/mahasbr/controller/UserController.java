 package com.mahasbr.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.DistrictMaster;

import com.mahasbr.entity.MstRegistryDetailsPageEntity;

import com.mahasbr.entity.MstMenu;
import com.mahasbr.entity.MstSubMenu;

import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.entity.VillageMaster;
import com.mahasbr.model.SearchBrnDto;
import com.mahasbr.response.MessageResponse;
import com.mahasbr.service.CommonService;

import com.mahasbr.service.MstRegistryDetailsPageService;
import com.mahasbr.util.StringUtils;
import com.mahasbr.service.MstMenuService;
import com.mahasbr.service.MstSubMenuService;


@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	CommonService commonService;
	
	@Autowired
	MstRegistryDetailsPageService mstRegistryDetailsPageService;

	@Autowired
	private MstMenuService service;

	@Autowired
	private MstSubMenuService subMenuService;

	@GetMapping("/getDistrictData")
	public ResponseEntity<?> getDistrictDetails() {
		List<DistrictMaster> district = commonService.getAllDistrict();
		return ResponseEntity.ok(new MessageResponse(" District List", district));
	}

	@GetMapping("/getDistrictByCode/{CensusDistrictCode}")
	public ResponseEntity<?> getDistrictDetailByDistrictCode(@PathVariable long CensusDistrictCode) throws Exception {
		DistrictMaster district = commonService.getAllDistrictDistrictCode(CensusDistrictCode);
		return ResponseEntity.ok(new MessageResponse(" District List by District Code ", district));
	}

	@GetMapping("/getTalukaByCode/{CensusDistrictCode}")
	public ResponseEntity<?> getTalukaDetailByDistrictCode(@PathVariable long CensusDistrictCode) throws Exception {
		List<TalukaMaster> taluka = commonService.getAllTalukaByDistrictCode(CensusDistrictCode);
		return ResponseEntity.ok(new MessageResponse(" Taluka List by District Code ", taluka));
	}

	@GetMapping("/getVillagetByCode/{censusTalukaCode}")
	public ResponseEntity<?> getVillageDetailByTalukaCode(@PathVariable long censusTalukaCode) throws Exception {
		List<VillageMaster> village = commonService.getAllVillageTalukaCode(censusTalukaCode);
		return ResponseEntity.ok(new MessageResponse(" village List by taluka Code ", village));
	}

	
	@PostMapping("/searchBRN")
	public ResponseEntity<List<MstRegistryDetailsPageEntity>> getSearchBRN(@RequestBody SearchBrnDto searchBrnDto) throws Exception {
		//List<VillageMaster> village = commonService.getAllVillageTalukaCode(censusTalukaCode);
		List<MstRegistryDetailsPageEntity> searchBRNAndEstablishmentDetails=new ArrayList<>();
		StringUtils stringUtils = new StringUtils();
		if(!searchBrnDto.getDistrict().isEmpty()) {
			DistrictMaster district = commonService.getAllDistrictDistrictCode(Long.parseLong(searchBrnDto.getDistrict()));
		    searchBRNAndEstablishmentDetails=mstRegistryDetailsPageService.getsearchBRNAndEstablishmentDetails(stringUtils.safeUpperCase(district.getDistrictName()),stringUtils.safeUpperCase(searchBrnDto.getBrnNo()),stringUtils.safeUpperCase(searchBrnDto.getNameOfEstablishmentOrOwner()));
		}
		
		 return new ResponseEntity<>(searchBRNAndEstablishmentDetails, HttpStatus.OK);
	}


	@GetMapping("/getMenusByUserId/{userId}")
	public List<MstMenu> getMenusByUserId(@PathVariable Long userId) {
		List<MstMenu> lstMstMenu = service.getMenusByUserId(userId);
		if (lstMstMenu == null) {
			return null;
		}
		return lstMstMenu;
	}

	@GetMapping("/getAllSubMenus")
	public List<MstSubMenu> getAllSubMenus() {
		return subMenuService.getAllSubMenus();
	}

}
