package com.mahasbr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistryMasterResponse {
    private Long id;
    private String registryNameEn;
    private String registryNameMr;
    private Boolean status;
}
