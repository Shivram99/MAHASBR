package com.mahasbr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistryApiAuthConfigResponse {

    private Long id;
    private String serviceName;
    private String serviceNameCode;
    private String authUrl;
    private String authUrlMethod;
    private String username;
    private String password;
    private String apiData;
    private String apiDataMethod;
    private Boolean active;
}
