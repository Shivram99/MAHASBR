package com.mahasbr.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.mahasbr.entity.ApiCallLog;
import com.mahasbr.repository.ApiCallLogRepository;

@Service
public class ApiLogService {

    private final ApiCallLogRepository repository;

    public ApiLogService(ApiCallLogRepository repository) {
        this.repository = repository;
    }

    public void log(String apiName, String url, String status, String response) {
        ApiCallLog log = ApiCallLog.builder()
                .apiName(apiName)
                .url(url)
                .status(status)
                .response(response != null && response.length() > 4000 ? response.substring(0, 4000) : response)
                .timestamp(LocalDateTime.now())
                .build();
        repository.save(log);
        System.out.println("📌 Logged API call: " + apiName + " -> " + status);
    }
}