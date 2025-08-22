package com.mahasbr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MenuResponseDto {
    private Integer menuId;
    private Integer menuCode;
    private String menuNameEnglish;
    private String menuNameMarathi;
    private String isActive;
    private String icon;
}