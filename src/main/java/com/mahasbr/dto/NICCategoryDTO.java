package com.mahasbr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NICCategoryDTO {
    private String categoryCode;
    private String description;
    private String isActive;
}
