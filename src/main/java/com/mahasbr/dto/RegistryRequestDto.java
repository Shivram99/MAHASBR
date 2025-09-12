package com.mahasbr.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistryRequestDto {

    @NotBlank(message = "Registry name is required")
    @Size(max = 200)
    private String registryName;

    private Boolean status = true;

    // List of users to assign
    private List<UserDto> users;
}