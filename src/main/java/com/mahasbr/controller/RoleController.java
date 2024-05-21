package com.mahasbr.controller;
import java.util.List;
import java.util.Optional;

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

@RestController
@RequestMapping("/admin")
public class RoleController {

	
	@Autowired
	private  RoleService roleService;


    @GetMapping("/getRoleById/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") Long id) {
        Optional<Role> role = roleService.findRoleById(id);
        return role.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/getAllRoles")
    public List<Role> getAllRoles() {
    	
    	
        return roleService.getAllRoles();
    }

    @PostMapping("/createRole")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role savedRole = roleService.saveOrUpdateRole(role);
        return ResponseEntity.status(HttpStatus.OK).body(savedRole);
    }

    @PutMapping("/updateRole/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable("id") Long id, @RequestBody Role role) {
        if (!roleService.findRoleById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        role.setId(id); // Ensure the ID is set for the correct role
        Role updatedRole = roleService.saveOrUpdateRole(role);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/deleteRole/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") Long id) {
        if (!roleService.findRoleById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}