package com.mahasbr.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
 
	/*
	 * @Autowired private CustomerServices customerService;
	 */
 
    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
         
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
         
     //   Customer customer = customerService.getCustomerByEmail(username);
         
        // process logics with customer
         
        super.onLogoutSuccess(request, response, authentication);
    }  
}