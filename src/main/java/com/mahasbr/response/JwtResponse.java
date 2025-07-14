package com.mahasbr.response;

import java.util.List;

import lombok.Data;


public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private String username;
  private List<String> roles;
  
  private Boolean isFirstTimeLogin;
  


public JwtResponse(String accessToken, Long id, String username,List<String> roles,Boolean isFirstTimeLogin) {
    this.token = accessToken;
    this.id = id;
    this.username = username;
    this.roles = roles;
    this.isFirstTimeLogin = isFirstTimeLogin;
  }

  public String getAccessToken() {
    return token;
  }

  public void setAccessToken(String accessToken) {
    this.token = accessToken;
  }

  public String getTokenType() {
    return type;
  }

  public void setTokenType(String tokenType) {
    this.type = tokenType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public List<String> getRoles() {
    return roles;
  }

public Boolean getIsFirstTimeLogin() {
	return isFirstTimeLogin;
}

public void setIsFirstTimeLogin(Boolean isFirstTimeLogin) {
	this.isFirstTimeLogin = isFirstTimeLogin;
}
}