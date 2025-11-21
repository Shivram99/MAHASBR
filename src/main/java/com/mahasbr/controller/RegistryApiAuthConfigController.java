package com.mahasbr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.dto.RegistryApiAuthConfigRequest;
import com.mahasbr.dto.RegistryApiAuthConfigResponse;
import com.mahasbr.service.RegistryApiAuthConfigService;
import com.mahasbr.util.ApiResponse;

@RestController
@RequestMapping("/api/registry-auth")
public class RegistryApiAuthConfigController {

    @Autowired
    private RegistryApiAuthConfigService service;

    @PostMapping
    public ResponseEntity<ApiResponse<RegistryApiAuthConfigResponse>> create(
            @RequestBody RegistryApiAuthConfigRequest request) {

        RegistryApiAuthConfigResponse response = service.create(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RegistryApiAuthConfigResponse>> update(
            @PathVariable Long id,
            @RequestBody RegistryApiAuthConfigRequest request) {

        RegistryApiAuthConfigResponse response = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Updated successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RegistryApiAuthConfigResponse>> getById(@PathVariable Long id) {

        RegistryApiAuthConfigResponse response = service.getById(id);
        return ResponseEntity.ok(ApiResponse.ok(response, "Record fetched"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RegistryApiAuthConfigResponse>>> getAll() {

        List<RegistryApiAuthConfigResponse> list = service.getAll();
        return ResponseEntity.ok(ApiResponse.ok(list, "List fetched"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Deleted Successfully", "Record deleted"));
    }
}
