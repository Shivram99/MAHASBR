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

import com.mahasbr.entity.MstMenuRoleMapping;
import com.mahasbr.entity.Role;
import com.mahasbr.model.MstMenuRoleMappingModel;
import com.mahasbr.service.MstMenuRoleMappingService;
import com.mahasbr.service.MstMenuService;
import com.mahasbr.service.RoleService;

@RestController
@RequestMapping("/developer")
public class MstMenuRoleMappingController {

    @Autowired
    private MstMenuRoleMappingService service;
    
    @Autowired
	private MstMenuService mstMenuService;
    
    
    @Autowired
    private RoleService roleService;
    

    @GetMapping("/MstMenuRoleMapping")
    public List<MstMenuRoleMapping> getAllMappings() {
        return service.getAllMappings();
    }

    @GetMapping("/getMappingById/{id}")
    public MstMenuRoleMapping getMappingById(@PathVariable Long id) {
        return service.getMappingById(id);
    }

    @PostMapping("/createMapping")
    public MstMenuRoleMapping createMapping(@RequestBody MstMenuRoleMappingModel mapping) {
    	MstMenuRoleMapping mstMenuRoleMapping=new MstMenuRoleMapping();
    	mstMenuRoleMapping.setRole(roleService.findRoleById(mapping.getRoleId()).get());
    	mstMenuRoleMapping.setMenu(mstMenuService.getMenuById(mapping.getMenuId()));
    	
    	//mstMenuService
        return service.createMapping(mstMenuRoleMapping);
    }

    @PutMapping("/updateMapping/{id}")
    public MstMenuRoleMapping updateMapping(@PathVariable Long id, @RequestBody MstMenuRoleMapping mapping) {
        return service.updateMapping(id, mapping);
    }

    @DeleteMapping("/deleteMapping/{id}")
    public void deleteMapping(@PathVariable Long id) {
        service.deleteMapping(id);
    }
    
    @GetMapping("/getAllRoles")
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }
}