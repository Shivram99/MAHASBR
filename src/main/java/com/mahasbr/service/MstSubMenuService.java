package com.mahasbr.service;

import java.util.List;

import com.mahasbr.entity.MstSubMenu;

public interface MstSubMenuService {

    List<MstSubMenu> getAllSubMenus();
    MstSubMenu getSubMenuById(Long id);
    MstSubMenu createSubMenu(MstSubMenu subMenu);
    MstSubMenu updateSubMenu(Long id, MstSubMenu subMenu);
    void deleteSubMenu(Long id);
    List<MstSubMenu> getSubMenusByUserRole(Long userRoleId);

}
