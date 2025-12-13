package com.mahasbr.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.mahasbr.dto.MenuDTO;
import com.mahasbr.entity.Menu;

@Component
public class MenuMapper {

	 public MenuDTO toDTO(Menu menu) {
	        if (menu == null) {
	            return null;
	        }

	        MenuDTO dto = new MenuDTO();
	        dto.setId(menu.getId());
	        dto.setNameEn(menu.getNameEn());
	        dto.setNameMr(menu.getNameMr());
	        dto.setRoute(menu.getRoute());
	        dto.setIcon(menu.getIcon());
	        dto.setSequence(menu.getSequence());
	        dto.setActive(menu.getActive());
	        dto.setMenuType(menu.getMenuType());

	        dto.setParentId(menu.getParent() != null ? menu.getParent().getId() : null);

	        // Convert children recursively
	        if (menu.getChildren() != null) {
	            dto.setChildren(
	                menu.getChildren().stream()
	                    .map(this::toDTO)
	                    .collect(Collectors.toList())
	            );
	        }

	        return dto;
	    }

}