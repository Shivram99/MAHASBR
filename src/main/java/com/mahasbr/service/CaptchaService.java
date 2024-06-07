package com.mahasbr.service;

import com.mahasbr.model.Captcha;

public interface CaptchaService {
    Captcha generateCaptcha();
    boolean verifyCaptcha(String captchaId, String userInput);
}