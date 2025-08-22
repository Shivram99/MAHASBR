package com.mahasbr.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mahasbr.dto.MenuRequestDto;
import com.mahasbr.dto.MenuResponseDto;
import com.mahasbr.entity.MstMenuEntity;
import com.mahasbr.repository.MstMenuRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

 private final MstMenuRepository menuRepository;

 @Override
 public MenuResponseDto createMenu(MenuRequestDto dto) {
     if (menuRepository.existsByMenuCode(dto.getMenuCode())) {
         throw new IllegalArgumentException("Menu code already exists");
     }
     MstMenuEntity entity = mapToEntity(dto);
     MstMenuEntity saved = menuRepository.save(entity);
     return mapToResponse(saved);
 }

 @Override
 public MenuResponseDto updateMenu(Integer id, MenuRequestDto dto) {
     MstMenuEntity entity = menuRepository.findById(id)
             .orElseThrow(() -> new EntityNotFoundException("Menu not found with id " + id));

     entity.setMenuCode(dto.getMenuCode());
     entity.setMenu_name_english(dto.getMenuNameEnglish());
     entity.setMenu_name_marathi(dto.getMenuNameMarathi());
     entity.setIs_active(dto.getIsActive());
     entity.setIcon(dto.getIcon());

     return mapToResponse(menuRepository.save(entity));
 }

 @Override
 public MenuResponseDto getMenuById(Integer id) {
     MstMenuEntity entity = menuRepository.findById(id)
             .orElseThrow(() -> new EntityNotFoundException("Menu not found with id " + id));
     return mapToResponse(entity);
 }

 @Override
 public List<MenuResponseDto> getAllMenus() {
     return menuRepository.findAll()
             .stream()
             .map(this::mapToResponse)
             .collect(Collectors.toList());
 }


 @Override
 public void deleteMenu(Integer id) {
     if (!menuRepository.existsById(id)) {
         throw new EntityNotFoundException("Menu not found with id " + id);
     }
     menuRepository.deleteById(id);
 }

 private MstMenuEntity mapToEntity(MenuRequestDto dto) {
     MstMenuEntity entity = new MstMenuEntity();
     entity.setMenuCode(dto.getMenuCode());
     entity.setMenu_name_english(dto.getMenuNameEnglish());
     entity.setMenu_name_marathi(dto.getMenuNameMarathi());
     entity.setIs_active(dto.getIsActive());
     entity.setIcon(dto.getIcon());
     return entity;
 }

 private MenuResponseDto mapToResponse(MstMenuEntity entity) {
     return new MenuResponseDto(
             entity.getMenuId(),
             entity.getMenuCode(),
             entity.getMenu_name_english(),
             entity.getMenu_name_marathi(),
             entity.getIs_active(),
             entity.getIcon()
     );
 }
}
