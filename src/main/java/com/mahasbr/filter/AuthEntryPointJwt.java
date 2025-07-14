package com.mahasbr.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

  private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
  
  private static String username=null;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {
    logger.error("Unauthorized error: {}", authException.getMessage());
   // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    
    logger.error("Unauthorized error: {}", authException.getMessage());
    

    if (authException instanceof org.springframework.security.authentication.BadCredentialsException) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Bad credentials");
    } else if (authException instanceof org.springframework.security.authentication.LockedException) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: User account is locked");
    } else if (authException instanceof org.springframework.security.authentication.DisabledException) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: User account is disabled");
    } else {
        // For any other type of exception, consider it as an internal server error
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: Internal server error");
    }
  }
}


/*

@Override
public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
    throws IOException, ServletException {
  logger.error("Unauthorized error: {}", authException.getMessage());

  response.setContentType(MediaType.APPLICATION_JSON_VALUE);
  response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

  final Map<String, Object> body = new HashMap<>();
  body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
  body.put("error", "Unauthorized");
  body.put("message", authException.getMessage());
  body.put("path", request.getServletPath());

  final ObjectMapper mapper = new ObjectMapper();
  mapper.writeValue(response.getOutputStream(), body);
}*/