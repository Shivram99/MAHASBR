package com.mahasbr.service;

import java.util.List;

import com.mahasbr.dto.MenuRequestDto;
import com.mahasbr.dto.MenuResponseDto;

public interface MenuService {
    MenuResponseDto createMenu(MenuRequestDto dto);
    MenuResponseDto updateMenu(Integer id, MenuRequestDto dto);
    MenuResponseDto getMenuById(Integer id);
    List<MenuResponseDto> getAllMenus();
    void deleteMenu(Integer id);
}