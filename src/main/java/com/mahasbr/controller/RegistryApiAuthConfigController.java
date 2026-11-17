package com.mahasbr.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.dto.RegistryApiAuthConfigRequest;
import com.mahasbr.dto.RegistryApiAuthConfigResponse;
import com.mahasbr.service.RegistryApiAuthConfigService;
import com.mahasbr.service.RegistrySyncService;
import com.mahasbr.util.ApiResponse;

import lombok.RequiredArgsConstructor;

/**
 * Registry API Settings (admin only, see WebSecurityConfig): which external registry APIs
 * exist, which are switched on, and "Run now".
 */
@RestController
@RequestMapping("/api/registry-auth")
@RequiredArgsConstructor
public class RegistryApiAuthConfigController {

    private final RegistryApiAuthConfigService service;
    private final RegistrySyncService syncService;

    @PostMapping
    public ResponseEntity<ApiResponse<RegistryApiAuthConfigResponse>> create(
            @RequestBody RegistryApiAuthConfigRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.create(request), "Created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RegistryApiAuthConfigResponse>> update(
            @PathVariable Long id,
            @RequestBody RegistryApiAuthConfigRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request), "Updated successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RegistryApiAuthConfigResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id), "Record fetched"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RegistryApiAuthConfigResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAll(), "List fetched"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Deleted Successfully", "Record deleted"));
    }

    /** Switch one API on or off. Body: {"active": true}. Only active APIs are ever called. */
    @PatchMapping("/{id}/active")
    public ResponseEntity<ApiResponse<RegistryApiAuthConfigResponse>> setActive(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        Boolean active = body.get("active");
        if (active == null) {
            throw new IllegalArgumentException("Send {\"active\": true} or {\"active\": false}.");
        }
        RegistryApiAuthConfigResponse response = service.setActive(id, active);
        return ResponseEntity.ok(ApiResponse.ok(response, active ? "API switched on" : "API switched off"));
    }

    /** Start a sync for this API now. Returns immediately; poll GET to follow its status. */
    @PostMapping("/{id}/run")
    public ResponseEntity<ApiResponse<RegistryApiAuthConfigResponse>> runNow(@PathVariable Long id) {
        syncService.startSync(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.ok(service.getById(id), "Sync started"));
    }
}
