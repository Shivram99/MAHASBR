package com.mahasbr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.service.StatesMasterService;

@RestController
@RequestMapping("/api/auth")
public class StatesMasterController {
	@Autowired
	StatesMasterService statesMasterService;

	/*
	 * @PostMapping("/state") public ResponseEntity<?> postStateName(@RequestBody
	 * StatesMasterModel stateMasterModel) { StatesMaster state =
	 * statesMasterService.postState(stateMasterModel); return ResponseEntity.ok(new
	 * MessageResponse("Added successfully!", state)); }
	 */

}
