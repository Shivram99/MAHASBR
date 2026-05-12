package com.mahasbr.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.dto.AssignMenuToRoleDTO;
import com.mahasbr.dto.MenuCreateDTO;
import com.mahasbr.dto.MenuDTO;
import com.mahasbr.entity.Role;
import com.mahasbr.service.MenuService;
import com.mahasbr.service.UserDetailsImpl;
import com.mahasbr.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/citizenSearch/menus")
@RequiredArgsConstructor
public class MenuController {

	private static final Logger logger = LoggerFactory.getLogger(MenuController.class);
	private final MenuService menuService;

	/*
	 * ============================= MENU CRUD API =============================
	 */

	@GetMapping
	public ResponseEntity<ApiResponse<List<MenuDTO>>> getAllMenus() {
		List<MenuDTO> menus = menuService.getAllMenus();
		return ResponseEntity.ok(new ApiResponse<>(true, "Menus fetched successfully", menus));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<MenuDTO>> getMenuById(@PathVariable Long id) {
		MenuDTO menu = menuService.getMenuById(id);
		return ResponseEntity.ok(new ApiResponse<>(true, "Menu fetched successfully", menu));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<MenuDTO>> createMenu(@RequestBody MenuCreateDTO dto) {

		MenuDTO created = menuService.createMenu(dto);
		return ResponseEntity.ok(new ApiResponse<>(true, "Menu created successfully", created));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<MenuDTO>> updateMenu(@PathVariable Long id, @RequestBody MenuCreateDTO dto) {

		MenuDTO updated = menuService.updateMenu(id, dto);
		return ResponseEntity.ok(new ApiResponse<>(true, "Menu updated successfully", updated));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteMenu(@PathVariable Long id) {
		menuService.deleteMenu(id);
		return ResponseEntity.ok(new ApiResponse<>(true, "Menu deleted successfully", null));
	}

	/*
	 * ============================= ROLE-BASED MENU =============================
	 */
	@GetMapping("/role/{role}")
	public ResponseEntity<ApiResponse<List<MenuDTO>>> getMenusByRole(@PathVariable Role role) {
		List<MenuDTO> menus = menuService.getMenusForRole(role);
		return ResponseEntity.ok(new ApiResponse<>(true, "Menus fetched for role " + role, menus));
	}

	/*
	 * ============================= ASSIGN MENU TO ROLE
	 * =============================
	 */
	@PostMapping("/assign")
	public ResponseEntity<ApiResponse<Void>> assignMenuToRole(@RequestBody AssignMenuToRoleDTO dto) {
		menuService.assignMenuToRole(dto.getMenuId(), dto.getRoleId());
		return ResponseEntity.ok(new ApiResponse<>(true, "Menu assigned to role successfully", null));
	}

	@PostMapping("/remove")
	public ResponseEntity<ApiResponse<Void>> removeMenuFromRole(@RequestBody AssignMenuToRoleDTO dto) {
		menuService.removeMenuFromRole(dto.getMenuId(), dto.getRoleId());
		return ResponseEntity.ok(new ApiResponse<>(true, "Menu removed from role successfully", null));
	}

	@GetMapping("/my")
	public ResponseEntity<ApiResponse<List<MenuDTO>>> getMyMenus(
	        Authentication authentication) {

	    // 1. Authentication null check
	    if (authentication == null || !authentication.isAuthenticated()) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(new ApiResponse<>(false, "Unauthorized", null));
	    }

	    // 2. Principal type check
	    Object principal = authentication.getPrincipal();
	    if (!(principal instanceof UserDetailsImpl userDetails)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(new ApiResponse<>(false, "Invalid user principal", null));
	    }

	    // 3. Roles safety
	    Set<Role> roles = userDetails.getRoles();
	    if (roles == null || roles.isEmpty()) {
	        return ResponseEntity.ok(
	                new ApiResponse<>(true, "No menus available", List.of())
	        );
	    }

	    // 4. Convert roles
	    Set<String> roleNames = roles.stream()
	            .map(Role::getName)
	            .collect(Collectors.toSet());

	    // 5. Load menus
	    List<MenuDTO> menus =
	            menuService.getMenusForRoles(roleNames);

	    return ResponseEntity.ok(
	            new ApiResponse<>(true, "Menus loaded successfully", menus)
	    );
	}


}
