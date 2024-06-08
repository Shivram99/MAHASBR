package com.mahasbr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.model.Captcha;
import com.mahasbr.model.CaptchaVerification;
import com.mahasbr.service.CaptchaService;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

	@Autowired
    private  CaptchaService captchaService;


    @GetMapping
    public Captcha generateCaptcha() {
        return captchaService.generateCaptcha();
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCaptcha(@RequestBody CaptchaVerification captchaVerification) {
        boolean isValid = captchaService.verifyCaptcha(captchaVerification.getId(), captchaVerification.getUserInput());
        if (isValid) {
            return ResponseEntity.ok("CAPTCHA is valid.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CAPTCHA is invalid.");
        }
    }
}