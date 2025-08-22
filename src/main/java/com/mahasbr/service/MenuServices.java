package com.mahasbr.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mahasbr.dto.MenuDTO;
import com.mahasbr.entity.Menu;
import com.mahasbr.entity.Role;
import com.mahasbr.repository.MenuRepository;
import com.mahasbr.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuServices {

	  private final MenuRepository menuRepository;
	    private final RoleRepository roleRepository;

	    public List<MenuDTO> getAllMenus() {
	        return menuRepository.findAll().stream()
	                .map(this::toDto)
	                .collect(Collectors.toList());
	    }

	    public MenuDTO getMenu(Long id) {
	        return menuRepository.findById(id)
	                .map(this::toDto)
	                .orElseThrow(() -> new RuntimeException("Menu not found"));
	    }

	    public MenuDTO createMenu(MenuDTO dto) {
	        Menu menu = toEntity(dto);
	        setRolesFromDto(menu, dto); // ✅ attach roles before saving
	        return toDto(menuRepository.save(menu));
	    }

	    public MenuDTO updateMenu(Long id, MenuDTO dto) {
	        Menu menu = menuRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Menu not found"));

	        menu.setNameEn(dto.getNameEn());
	        menu.setNameMh(dto.getNameMh());
	        menu.setUrl(dto.getUrl());
	        menu.setMenuOrder(dto.getMenuOrder());
	        menu.setActive(dto.getActive());
	        menu.setParent(dto.getParentId() != null
	                ? menuRepository.findById(dto.getParentId()).orElse(null)
	                : null);

	        setRolesFromDto(menu, dto); // ✅ update role mapping

	        return toDto(menuRepository.save(menu));
	    }


	    public void deleteMenu(Long id) {
	        menuRepository.deleteById(id);
	    }

	    // ✅ Safe DTO mapping
	    private MenuDTO toDto(Menu menu) {
	        return MenuDTO.builder()
	                .id(menu.getId())
	                .nameEn(menu.getNameEn())
	                .nameMh(menu.getNameMh())
	                .url(menu.getUrl())
	                .menuOrder(menu.getMenuOrder())
	                .active(menu.getActive())
	                .parentId(menu.getParent() != null ? menu.getParent().getId() : null)
	                .roleIds(menu.getRoles() != null
	                        ? menu.getRoles().stream().map(Role::getId).collect(Collectors.toList())
	                        : new ArrayList<>())
	                .build();
	    }

	    private Menu toEntity(MenuDTO dto) {
	        Menu menu = new Menu();
	        menu.setId(dto.getId());
	        menu.setNameEn(dto.getNameEn());
	        menu.setNameMh(dto.getNameMh());
	        menu.setUrl(dto.getUrl());
	        menu.setMenuOrder(dto.getMenuOrder());
	        menu.setActive(dto.getActive());
	        menu.setParent(dto.getParentId() != null
	                ? menuRepository.findById(dto.getParentId()).orElse(null)
	                : null);
	        return menu;
	    }
	    public List<MenuDTO> getMenusByRole(Long roleId) {
	        return menuRepository.findDistinctByRoleId(roleId)
	                .stream()
	                .map(this::toDto) // Your existing toDto mapper
	                .collect(Collectors.toList());
	    }
	    private void setRolesFromDto(Menu menu, MenuDTO dto) {
	        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
	            Set<Role> roles = new HashSet<>(roleRepository.findAllById(dto.getRoleIds()));
	            menu.setRoles(roles);
	        } else {
	            menu.setRoles(new HashSet<>()); // ensure no null
	        }
	    }
}
