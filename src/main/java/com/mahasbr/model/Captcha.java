package com.mahasbr.model;

import lombok.Data;

@Data
public class Captcha {
    private String id;
    private String imageUrl;
    private String code;

    public Captcha(String id, String code) {
        this.id = id;
        this.code = code;
    }

}
