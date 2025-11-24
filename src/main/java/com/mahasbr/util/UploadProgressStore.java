package com.mahasbr.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class UploadProgressStore {

	private final Map<String, Integer> progress = new ConcurrentHashMap<>();

    public void set(String fileId, int percent) {
        progress.put(fileId, percent);
    }

    public int get(String fileId) {
        return progress.getOrDefault(fileId, 0);
    }

    public void remove(String fileId) {
        progress.remove(fileId);
    }
}
