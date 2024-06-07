package com.mahasbr.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mahasbr.model.Captcha;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    private Map<String, String> captchaStorage = new HashMap<>();

    @Override
    public Captcha generateCaptcha() {
        // Generate CAPTCHA code
        String captchaCode = generateCaptchaCode();
        
        // Store CAPTCHA code with a unique ID
        String captchaId = UUID.randomUUID().toString();
        captchaStorage.put(captchaId, captchaCode);
        
        // Return CAPTCHA with ID
        return new Captcha(captchaId, captchaCode);
    }

    @Override
    public boolean verifyCaptcha(String captchaId, String userInput) {
        // Retrieve CAPTCHA code associated with the provided ID
        String captchaCode = captchaStorage.get(captchaId);
        if (captchaCode != null) {
            return userInput.equals(captchaCode);
        }
        return false;
    }

    private String generateCaptchaCode() {
    	 String captchaCode = generateCaptchaCode();
         
         // Store CAPTCHA code with a unique ID
         String captchaId = UUID.randomUUID().toString();
         return captchaId;
    }
}