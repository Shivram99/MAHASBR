package com.mahasbr.dto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * Stores the list of successfully saved rows for each uploaded file.
 * Key = fileId (unique for each upload)
 * Value = List of RegistryRowDTO (only valid rows that were saved)
 *
 * This helps the frontend retrieve saved records AFTER the async upload completes.
 */
@Component
public class UploadResultRecordes {

    private final Map<String, List<RegistryRowDTO>> store = new ConcurrentHashMap<>();

    /** Save list of rows for a specific fileId */
    public void set(String fileId, List<RegistryRowDTO> rows) {
        store.put(fileId, rows);
    }

    /** Get saved rows for a fileId */
    public List<RegistryRowDTO> get(String fileId) {
        return store.getOrDefault(fileId, List.of());
    }

    /** Clear records for a fileId (if needed) */
    public void clear(String fileId) {
        store.remove(fileId);
    }
}
