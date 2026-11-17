package com.mahasbr.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoggedInUserResponse {

    private String username;
    private String fullName;
    private String email;
    private List<String> roles;
    private String activeRole;
    /** True while the user is still on a temporary password; the frontend sends them to change it. */
    private Boolean isFirstTimeLogin;
}
