package com.mahasbr.service;

//src/main/java/com/example/service/SubMenuService.java
import java.util.List;

import com.mahasbr.dto.SubMenuRequestDto;
import com.mahasbr.dto.SubMenuResponseDto;

public interface SubMenuService {
 SubMenuResponseDto createSubMenu(SubMenuRequestDto dto);
 SubMenuResponseDto updateSubMenu(Integer id, SubMenuRequestDto dto);
 SubMenuResponseDto getSubMenuById(Integer id);
 List<SubMenuResponseDto> getAllSubMenus();
 void deleteSubMenu(Integer id);
}
