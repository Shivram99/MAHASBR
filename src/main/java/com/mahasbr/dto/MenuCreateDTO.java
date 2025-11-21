package com.mahasbr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuCreateDTO {
    private Long parentId;
    private String nameEn;
    private String nameMr;
    private String route;
    private String icon;
    private String menuType;
    private Integer sequence;
    private Boolean active = true;
}