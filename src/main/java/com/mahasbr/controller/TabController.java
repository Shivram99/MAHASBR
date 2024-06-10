package com.mahasbr.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.model.TopicModel;
import com.mahasbr.service.CommonService;

@RestController
@RequestMapping("/user")
public class TabController {

	@Autowired
	CommonService commonService;

	@GetMapping("/getAllMenuByName/{roleId}")
	public ResponseEntity<List<TopicModel>> getAllMenuByName(@PathVariable Long roleId) {
		List<TopicModel> menuList = new ArrayList<>();
		List<TopicModel> subMenuList = new ArrayList<>();
		menuList = commonService.findMenuNameByRoleID(roleId);
		return ResponseEntity.ok(menuList);
	}
	
	@GetMapping("/getAllSubMenu/{roleId}")
	public ResponseEntity<List<TopicModel>> getAllSubMenu(@PathVariable Long roleId) {
		List<TopicModel> subMenuList = new ArrayList<>();
		subMenuList = commonService.findSubMenuByRoleID(roleId);
		return ResponseEntity.ok(subMenuList);
	}

}
