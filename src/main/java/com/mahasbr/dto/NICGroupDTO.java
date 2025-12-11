package com.mahasbr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NICGroupDTO {
    private String groupCode;
    private String description;
    private String divisionCode;   // parent id only (no nested object)
    private String isActive;
}