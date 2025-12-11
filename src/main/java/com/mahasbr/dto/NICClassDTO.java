package com.mahasbr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NICClassDTO {

    private String classCode;
    private String description;
    private String groupCode;
    private String isActive;
}
