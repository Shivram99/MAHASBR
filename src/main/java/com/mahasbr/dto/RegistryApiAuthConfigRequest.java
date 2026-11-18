package com.mahasbr.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistryApiAuthConfigRequest {

    private String serviceName;
    private String serviceNameCode;
    private String authUrl;
    private String authUrlMethod;
    private String username;
    /** Required when creating. On update, leave empty to keep the stored password. */
    private String password;
    private String apiData;
    private String apiDataMethod;
    private Boolean active;
    /** TOKEN or BASIC (default TOKEN). */
    private String authType;
    private String dateFromField;
    private String dateToField;
    /** Date pattern, e.g. yyyy-MM-dd or yyyyMMdd. */
    private String dateFormat;
    /** Accept any HTTPS certificate from this API (unsafe; off by default). */
    private Boolean skipSslVerify;
    /** mst_registry_master.id the data belongs to. */
    private Long registryId;
    /** First date to fetch on the very first sync (default 2015-01-01). */
    private LocalDate syncFromDate;
}
