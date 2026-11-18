package com.mahasbr.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** What the settings page sees. The password itself is never included. */
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
    /** True if a password is stored (the value itself is never returned). */
    private boolean passwordSet;
    private String apiData;
    private String apiDataMethod;
    private Boolean active;
    private String authType;
    private String dateFromField;
    private String dateToField;
    private String dateFormat;
    private Boolean skipSslVerify;

    private Long registryId;
    private String registryName;
    private LocalDate syncFromDate;
    private LocalDate lastSyncedTo;

    private LocalDateTime lastRunAt;
    private String lastRunStatus;
    private String lastRunMessage;
    /** True while a sync for this API is in progress right now. */
    private boolean running;
}
