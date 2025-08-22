package com.mahasbr.controller;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.dto.MenuRequestDto;
import com.mahasbr.dto.MenuResponseDto;
import com.mahasbr.service.MenuService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menus1")
@RequiredArgsConstructor
public class MenuController {

 private final MenuService menuService;

 @PostMapping
 public ResponseEntity<MenuResponseDto> createMenu(@Valid @RequestBody MenuRequestDto dto) {
     return ResponseEntity.status(HttpStatus.CREATED).body(menuService.createMenu(dto));
 }

 @PutMapping("/{id}")
 public ResponseEntity<MenuResponseDto> updateMenu(@PathVariable Integer id,
                                                   @Valid @RequestBody MenuRequestDto dto) {
     return ResponseEntity.ok(menuService.updateMenu(id, dto));
 }

 @GetMapping("/{id}")
 public ResponseEntity<MenuResponseDto> getMenuById(@PathVariable Integer id) {
     return ResponseEntity.ok(menuService.getMenuById(id));
 }

 @GetMapping
 public ResponseEntity<List<MenuResponseDto>> getAllMenus() {
     return ResponseEntity.ok(menuService.getAllMenus());
 }
 @DeleteMapping("/{id}")
 public ResponseEntity<Void> deleteMenu(@PathVariable Integer id) {
     menuService.deleteMenu(id);
     return ResponseEntity.noContent().build();
 }
}
