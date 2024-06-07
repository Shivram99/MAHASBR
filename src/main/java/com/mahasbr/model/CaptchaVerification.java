package com.mahasbr.model;

import lombok.Data;

@Data
public class CaptchaVerification {
    private String id;
    private String userInput;

    public CaptchaVerification() {
    }

    public CaptchaVerification(String id, String userInput) {
        this.id = id;
        this.userInput = userInput;
    }

}
