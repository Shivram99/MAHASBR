package com.mahasbr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for DivisionMaster entity
 * Used to expose only necessary fields to the API layer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DivisionDto {

    private Long divisionId;

    @NotBlank(message = "Division name cannot be blank")
    private String divisionName;

    @NotBlank(message = "Division code cannot be blank")
    private String divisionCode;

    @NotNull(message = "Active status is required")
    private Boolean isActive;
}
