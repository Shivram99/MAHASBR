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

import com.mahasbr.entity.MstSubMenu;
import com.mahasbr.service.MstSubMenuService;

@RestController
@RequestMapping("/developer")
public class MstSubMenuController {
    @Autowired
    private MstSubMenuService subMenuService;

    @GetMapping("/getAllSubMenus")
    public List<MstSubMenu> getAllSubMenus() {
        return subMenuService.getAllSubMenus();
    }

    @GetMapping("/getSubMenuById/{id}")
    public MstSubMenu getSubMenuById(@PathVariable Long id) {
        return subMenuService.getSubMenuById(id);
    }

    @PostMapping("/createSubMenu}")
    public MstSubMenu createSubMenu(@RequestBody MstSubMenu subMenu) {
        return subMenuService.createSubMenu(subMenu);
    }

    @PutMapping("/updateSubMenu/{id}")
    public MstSubMenu updateSubMenu(@PathVariable Long id, @RequestBody MstSubMenu subMenu) {
        return subMenuService.updateSubMenu(id, subMenu);
    }

    @DeleteMapping("/deleteSubMenu/{id}")
    public void deleteSubMenu(@PathVariable Long id) {
        subMenuService.deleteSubMenu(id);
    }

    @GetMapping("/getSubMenusByUserRole/{userRoleId}")
    public List<MstSubMenu> getSubMenusByUserRole(@PathVariable Long userRoleId) {
        return subMenuService.getSubMenusByUserRole(userRoleId);
    }
}
