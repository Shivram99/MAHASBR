package com.mahasbr.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.dto.AssignMenuDTO;
import com.mahasbr.dto.MenuDTO;
import com.mahasbr.service.MenuPermissionService;
import com.mahasbr.util.ApiResponse;

@RestController
@RequestMapping("/api/permissions")
public class MenuPermissionController {


    private static final Logger logger = LoggerFactory.getLogger(MenuPermissionController.class);

    private final MenuPermissionService permissionService;

    public MenuPermissionController(MenuPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /*
     =========================================================
                     ROLE ↔ MENU ASSIGNMENT
     =========================================================
    */

    @PostMapping("/role/assign")
    public ResponseEntity<ApiResponse<Void>> assignMenuToRole(@RequestBody AssignMenuDTO dto) {
        logger.info("Assigning menu {} to role {}", dto.getMenuIds(), dto.getRoleId());

        permissionService.assignMenuToRole(dto);

        return ResponseEntity.ok(ApiResponse.ok(null, "Menu assigned to role successfully."));
    }

    @DeleteMapping("/role/remove")
    public ResponseEntity<ApiResponse<Void>> removeMenuFromRole(@RequestBody AssignMenuDTO dto) {
        logger.info("Removing menu {} from role {}", dto.getMenuIds(), dto.getRoleId());

        permissionService.removeMenuFromRole(dto);

        return ResponseEntity.ok(ApiResponse.ok(null, "Menu removed from role successfully."));
    }

    @GetMapping("/role/{roleId}/menus")
    public ResponseEntity<ApiResponse<List<MenuDTO>>> getMenusForRole(@PathVariable Long roleId) {
        logger.info("Fetching menus assigned to role {}", roleId);

        List<MenuDTO> menus = permissionService.getMenusForRole(roleId);

        return ResponseEntity.ok(ApiResponse.ok(menus, "Fetched role menu mapping successfully."));
    }

    /*
     =========================================================
                     USER ↔ MENU ASSIGNMENT
     =========================================================
    */

    @PostMapping("/user/assign")
    public ResponseEntity<ApiResponse<Void>> assignMenuToUser(@RequestBody AssignMenuDTO dto) {
        logger.info("Assigning menu {} to user {}", dto.getMenuIds(), dto.getUserId());

        permissionService.assignMenuToUser(dto);

        return ResponseEntity.ok(ApiResponse.ok(null, "Menu assigned to user successfully."));
    }

    @DeleteMapping("/user/remove")
    public ResponseEntity<ApiResponse<Void>> removeMenuFromUser(@RequestBody AssignMenuDTO dto) {
        logger.info("Removing menu {} from user {}", dto.getMenuIds(), dto.getUserId());

        permissionService.removeMenuFromUser(dto);

        return ResponseEntity.ok(ApiResponse.ok(null, "Menu removed from user successfully."));
    }

    @GetMapping("/user/{userId}/menus")
    public ResponseEntity<ApiResponse<List<MenuDTO>>> getMenusForUser(@PathVariable Long userId) {
        logger.info("Fetching menus for user {}", userId);

        List<MenuDTO> menus = permissionService.getMenusForUser(userId);

        return ResponseEntity.ok(ApiResponse.ok(menus, "Fetched user mapped menus successfully."));
    }
}
