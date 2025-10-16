package com.mahasbr.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/translate")
public class GoogleInputController {
	
	@GetMapping("/marathi")
    public ResponseEntity<String> translateToMarathi(@RequestParam String text) throws IOException {
        String url = "https://inputtools.google.com/request?text="
                + URLEncoder.encode(text, StandardCharsets.UTF_8)
                + "&itc=mr-t-i0-und&num=5&ie=utf-8&oe=utf-8&app=demopage";

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return ResponseEntity.ok(response.toString());
        }
    }
}
