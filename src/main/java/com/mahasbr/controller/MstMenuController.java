package com.mahasbr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.MstMenu;
import com.mahasbr.service.MstMenuService;

@RestController
@RequestMapping("/developer")
public class MstMenuController {

	@Autowired
	private MstMenuService service;

	@GetMapping("getAllMenus")
	public List<MstMenu> getAllMenus() {
		return service.getAllMenus();
	}

	@GetMapping("/getMenuById/{id}")
	public MstMenu getMenuById(@PathVariable Long id) {
		return service.getMenuById(id);
	}

	@PostMapping("/createMenu")
	public MstMenu createMenu(@RequestBody MstMenu menu) {
		return service.createMenu(menu);
	}

	@PutMapping("/updateMenu/{id}")
	public MstMenu updateMenu(@PathVariable Long id, @RequestBody MstMenu menu) {
		return service.updateMenu(id, menu);
	}

	@DeleteMapping("/deleteMenu/{id}")
	public void deleteMenu(@PathVariable Long id) {
		service.deleteMenu(id);
	}

	@GetMapping("/getMenusByUserId/{userId}")
	public List<MstMenu> getMenusByUserId(Long userId) {
		List<MstMenu> lstMstMenu = service.getMenusByUserId(userId);
		if (lstMstMenu == null) {
			return null;
		}
		return lstMstMenu;
	}
}
