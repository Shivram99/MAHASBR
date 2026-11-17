package com.mahasbr.dto;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    @NotEmpty(message = "Username is required")
    @Size(max = 20, message = "Username must be at most 20 characters")
    private String username;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 50, message = "Email must be at most 50 characters")
    private String email;

    @NotNull(message = "At least one role must be selected")
    @Size(min = 1, message = "At least one role must be selected")
    private Set<String> roles;

    private Boolean isFirstTimeLogin;

    /**
     * Set only in the response to "create user", so the admin can hand it to the user once.
     * READ_ONLY: a client can never send it in. It is never stored in plain text.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String temporaryPassword;

    private String divisionCode;
    
    private Long divisionId;

    private Long registryId;  // maps to registry1.id

    private Long districtId;  // maps to district.id
    
    private Long censusDistrictCode; 
    
    @Valid
    @NotNull(message = "User profile is required")
    private UserProfileDto userProfile;
}
