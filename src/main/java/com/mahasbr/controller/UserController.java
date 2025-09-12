package com.mahasbr.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.dto.RoleDto;
import com.mahasbr.dto.UserDto;
import com.mahasbr.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
//	@Autowired
//	CommonService commonService;
//
//	@Autowired
//	MstRegistryDetailsPageService mstRegistryDetailsPageService;
//
//	@GetMapping("/getDistrictData")
//	public ResponseEntity<?> getDistrictDetails() {
//		List<DistrictMaster> district = commonService.getAllDistrict();
//		return ResponseEntity.ok(new MessageResponse(" District List", district));
//	}
//
//	@GetMapping("/getDistrictByCode/{CensusDistrictCode}")
//	public ResponseEntity<?> getDistrictDetailByDistrictCode(@PathVariable String CensusDistrictCode) throws Exception {
//		DistrictMaster district = commonService.getAllDistrictDistrictCode(CensusDistrictCode);
//		return ResponseEntity.ok(new MessageResponse(" District List by District Code ", district));
//	}
//
//	@GetMapping("/getTalukaByCode/{CensusDistrictCode}")
//	public ResponseEntity<?> getTalukaDetailByDistrictCode(@PathVariable String CensusDistrictCode) throws Exception {
//		List<TalukaMaster> taluka = commonService.getAllTalukaByDistrictCode(CensusDistrictCode);
//		return ResponseEntity.ok(new MessageResponse(" Taluka List by District Code ", taluka));
//	}
//
//	@GetMapping("/getVillagetByCode/{censusTalukaCode}")
//	public ResponseEntity<?> getVillageDetailByTalukaCode(@PathVariable String censusTalukaCode) throws Exception {
//		List<VillageMaster> village = commonService.getAllVillageTalukaCode(censusTalukaCode);
//		return ResponseEntity.ok(new MessageResponse(" village List by taluka Code ", village));
//	}
//
//	@PostMapping("/searchBRN")
//	public ResponseEntity<List<MstRegistryDetailsPageEntity>> getSearchBRN(@RequestBody SearchBrnDto searchBrnDto)
//			throws Exception {
//		// List<VillageMaster> village =
//		// commonService.getAllVillageTalukaCode(censusTalukaCode);
//		List<MstRegistryDetailsPageEntity> searchBRNAndEstablishmentDetails = new ArrayList<>();
//		StringUtils stringUtils = new StringUtils();
//		if (!searchBrnDto.getDistrict().isEmpty()) {
//			DistrictMaster district = commonService
//					.getAllDistrictDistrictCode(Long.parseLong(searchBrnDto.getDistrict()));
//			searchBRNAndEstablishmentDetails = mstRegistryDetailsPageService.getsearchBRNAndEstablishmentDetails(
//					stringUtils.safeUpperCase(district.getDistrictName()),
//					stringUtils.safeUpperCase(searchBrnDto.getBrnNo()),
//					stringUtils.safeUpperCase(searchBrnDto.getNameOfEstablishmentOrOwner()));
//		}
//
//		return new ResponseEntity<>(searchBRNAndEstablishmentDetails, HttpStatus.OK);
//	}

	private final UserService userService;

	@GetMapping
	public List<UserDto> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getUserById(id));
	}

	@PostMapping
	public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) {
		return ResponseEntity.ok(userService.createUser(dto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto dto) {
		return ResponseEntity.ok(userService.updateUser(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/role")
    public List<RoleDto> getAllRoles() {
        return userService.getAllRoles();
    }
}
