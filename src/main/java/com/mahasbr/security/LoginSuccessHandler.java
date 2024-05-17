package com.mahasbr.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	/*
	 * @Autowired private CustomerServices customerService;
	 */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
 
        UserDetails customerDetails = (UserDetails) authentication.getPrincipal();
        
        
		/*
		 * Customer customer = customerDetails.getCustomer();
		 * 
		 * if (customer.isOTPRequired()) { customerService.clearOTP(customer); }
		 */
         
        super.onAuthenticationSuccess(request, response, authentication);
    }
 
}