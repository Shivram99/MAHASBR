package com.mahasbr.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mahasbr.dto.SubMenuRequestDto;
import com.mahasbr.dto.SubMenuResponseDto;
import com.mahasbr.entity.MstSubMenuEntity;
import com.mahasbr.repository.MstSubMenuRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubMenuServiceImpl implements SubMenuService {

	private final MstSubMenuRepository subMenuRepository;

	@Override
	public SubMenuResponseDto createSubMenu(SubMenuRequestDto dto) {
		if (subMenuRepository.existsBySubMenuCode(dto.getSubMenuCode())) {
			throw new IllegalArgumentException("Sub-menu code already exists");
		}
		MstSubMenuEntity entity = mapToEntity(dto);
		return mapToResponse(subMenuRepository.save(entity));
	}

	@Override
	public SubMenuResponseDto updateSubMenu(Integer id, SubMenuRequestDto dto) {
		MstSubMenuEntity entity = subMenuRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Sub-menu not found"));
		entity.setMenuCode(dto.getMenuCode());
		entity.setSubMenuCode(dto.getSubMenuCode());
		entity.setRoleId(dto.getRoleId());
		entity.setSub_menu_name_english(dto.getSubMenuNameEnglish());
		entity.setController_name(dto.getControllerName());
		entity.setLink_name(dto.getLinkName());
		entity.setSub_menu_name_marathi(dto.getSubMenuNameMarathi());
		entity.setIs_active(dto.getIsActive());
		entity.setIcon(dto.getIcon());
		return mapToResponse(subMenuRepository.save(entity));
	}

	@Override
	public SubMenuResponseDto getSubMenuById(Integer id) {
		return subMenuRepository.findById(id).map(this::mapToResponse)
				.orElseThrow(() -> new IllegalArgumentException("Sub-menu not found"));
	}

	@Override
	public List<SubMenuResponseDto> getAllSubMenus() {
		return subMenuRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	public void deleteSubMenu(Integer id) {
		if (!subMenuRepository.existsById(id)) {
			throw new IllegalArgumentException("Sub-menu not found");
		}
		subMenuRepository.deleteById(id);
	}

	// Utility methods
	private MstSubMenuEntity mapToEntity(SubMenuRequestDto dto) {
		MstSubMenuEntity entity = new MstSubMenuEntity();
		entity.setMenuCode(dto.getMenuCode());
		entity.setSubMenuCode(dto.getSubMenuCode());
		entity.setRoleId(dto.getRoleId());
		entity.setSub_menu_name_english(dto.getSubMenuNameEnglish());
		entity.setController_name(dto.getControllerName());
		entity.setLink_name(dto.getLinkName());
		entity.setSub_menu_name_marathi(dto.getSubMenuNameMarathi());
		entity.setIs_active(dto.getIsActive());
		entity.setIcon(dto.getIcon());
		return entity;
	}

	private SubMenuResponseDto mapToResponse(MstSubMenuEntity entity) {
		return new SubMenuResponseDto(entity.getSubMenuId(), entity.getMenuCode(), entity.getSubMenuCode(),
				entity.getRoleId(), entity.getSub_menu_name_english(), entity.getController_name(),
				entity.getLink_name(), entity.getSub_menu_name_marathi(), entity.getIs_active(), entity.getIcon());
	}
}
