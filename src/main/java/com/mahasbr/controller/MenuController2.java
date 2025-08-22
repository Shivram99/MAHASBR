package com.mahasbr.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.dto.MenuDTO;
import com.mahasbr.service.MenuServices;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MenuController2 {

    private final MenuServices menuService;

    @GetMapping
    public List<MenuDTO> getAllMenus() {
        return menuService.getAllMenus();
    }

    @GetMapping("/{id}")
    public MenuDTO getMenu(@PathVariable Long id) {
        return menuService.getMenu(id);
    }

    @PostMapping
    public MenuDTO createMenu(@RequestBody MenuDTO dto) {
        return menuService.createMenu(dto);
    }

    @PutMapping("/{id}")
    public MenuDTO updateMenu(@PathVariable Long id, @RequestBody MenuDTO dto) {
        return menuService.updateMenu(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
    }

    @GetMapping("/role/{roleId}")
    public List<MenuDTO> getMenusByRole(@PathVariable Long roleId) {
        return menuService.getMenusByRole(roleId);
    }
}
