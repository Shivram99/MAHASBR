package com.mahasbr.service;

import lombok.Getter;

@Getter
public enum ApiAuthConfig {

    CHARITY("https://sbrlmsapi.mahaitgov.in/api/Login", "axe2Zf3HlSBr5L6", "7FVaTuMIarLMAso"),
    DISH("https://sbrlmsapi.mahaitgov.in/api/Login", "axe2Zf3HlSBr5L6", "7FVaTuMIarLMAso"),
    LABOUR("https://sbrlmsapi.mahaitgov.in/api/Login", "axe2Zf3HlSBr5L6", "7FVaTuMIarLMAso"),
    COOP("https://sbrlmsapi.mahaitgov.in/api/Login", "coopUser123", "coopPass123"),
    KVIB("https://sbrlmsapi.mahaitgov.in/api/Login", "kvibUser123", "kvibPass123"),
    MCGM("https://sbrlmsapi.mahaitgov.in/api/Login", "mcgmUser123", "mcgmPass123"),
    MSME("https://sbrlmsapi.mahaitgov.in/api/Login", "msmeUser123", "msmePass123"),
    MCA("https://sbrlmsapi.mahaitgov.in/api/Login", "mcaUser123", "mcaPass123"),
    GSTN("https://sbrlmsapi.mahaitgov.in/api/Login", "gstnUser123", "gstnPass123");

    private final String loginUrl;
    private final String username;
    private final String password;

    ApiAuthConfig(String loginUrl, String username, String password) {
        this.loginUrl = loginUrl;
        this.username = username;
        this.password = password;
    }
}