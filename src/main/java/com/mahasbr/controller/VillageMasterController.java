package com.mahasbr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.VillageMaster;
import com.mahasbr.model.VillageMasterModel;
import com.mahasbr.response.MessageResponse;
import com.mahasbr.service.VillageMasterService;

@RestController
@RequestMapping("/api/auth")
public class VillageMasterController {
	@Autowired
	VillageMasterService villageMasterService;

	@PostMapping("/village")
	public ResponseEntity<?> postVillageDetails(@RequestBody VillageMasterModel villageMasterModel) {
		VillageMaster village = villageMasterService.insertVillageDetails(villageMasterModel);
		return ResponseEntity.ok(new MessageResponse("Added successfully!", village));
	}
}
