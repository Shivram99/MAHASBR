package com.mahasbr.mapper;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.mahasbr.dto.MenuDTO;
import com.mahasbr.entity.Menu;

@Component
public class MenuMapper {

	public MenuDTO toDto(Menu menu) {
	    MenuDTO dto = new MenuDTO();
	    dto.setId(menu.getId());
	    dto.setNameEn(menu.getNameEn());
	    dto.setNameMr(menu.getNameMr());
	    dto.setRoute(menu.getRoute());
	    dto.setIcon(menu.getIcon());
	    dto.setSequence(menu.getSequence());
	    dto.setActive(menu.getActive());

	    // Only set parentId—not children
	    dto.setParentId(menu.getParent() != null ? menu.getParent().getId() : null);

	    // Children will be assigned manually in service
	    dto.setChildren(new ArrayList<>());

	    return dto;
	}

}