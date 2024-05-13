package com.mahasbr.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.model.TalukaMasterModel;
import com.mahasbr.response.MessageResponse;
import com.mahasbr.service.TalukaMasterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
public class TalukaMasterController {
	@Autowired
	TalukaMasterService talukaMasterService;

	@PostMapping("/taluka")
	public ResponseEntity<MessageResponse> postMethodName(@RequestBody TalukaMasterModel talukaMasterModel) {
		TalukaMaster taluka = talukaMasterService.insertTalukaDetails(talukaMasterModel);
		return ResponseEntity.ok(new MessageResponse("Added successfully!", taluka));

	}

}
