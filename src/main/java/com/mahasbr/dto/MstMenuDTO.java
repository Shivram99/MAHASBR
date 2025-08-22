package com.mahasbr.dto;


import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for transferring Menu data to the frontend.
 * Includes validation to enforce required fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MstMenuDTO {

    private Long menuId; // Not mandatory for creation (auto-generated)

    @NotBlank(message = "Label is required")
    @Size(max = 100, message = "Label cannot exceed 100 characters")
    private String label;

    @Size(max = 50, message = "Icon name cannot exceed 50 characters")
    private String icon;

    @NotBlank(message = "URL is required")
    @Size(max = 200, message = "URL cannot exceed 200 characters")
    private String url;

    @Size(max = 200, message = "Router link cannot exceed 200 characters")
    private String routerLink;

    @Size(max = 100, message = "Controller name cannot exceed 100 characters")
    private String controllerName;

    @Size(max = 100, message = "Method name cannot exceed 100 characters")
    private String methodName;

    /**
     * Flag for soft delete (active or inactive menu).
     */
    @NotNull(message = "Active flag must not be null")
    private Boolean active;

    /**
     * Optional fine-grained permission.
     */
    @Size(max = 100, message = "Permission key cannot exceed 100 characters")
    private String permissionKey;

    /**
     * Nested children menus (for multi-level hierarchy).
     */
    private List<MstMenuDTO> children;

    /**
     * Roles assigned to this menu.
     * Roles are represented by their names for frontend use.
     */
    private Set<@NotBlank(message = "Role name cannot be blank") String> roles;

}
