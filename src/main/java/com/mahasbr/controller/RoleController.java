package com.mahasbr.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.mahasbr.entity.Role;
import com.mahasbr.service.RoleService;
import com.mahasbr.util.ApiResponse;
@RestController
@RequestMapping("/admin/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        return ResponseEntity.ok(ApiResponse.ok(roleService.getAllRoles(), "Roles fetched successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.ok(role, "Role fetched successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody Role role) {
        Role created = roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(created, "Role created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> updateRole(
            @PathVariable Long id,
            @RequestBody Role role) {

        Role updated = roleService.updateRole(id, role);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Role updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Role deleted successfully"));
    }
}
