package com.mahasbr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.NICCodeEntity;
import com.mahasbr.service.NICCodeService;
import com.mahasbr.util.ApiResponse;

@RestController
@RequestMapping("/api/nic-codes")
public class NICCodeController {

    @Autowired
    private NICCodeService nicCodeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NICCodeEntity>>> getAllCodes() {
        List<NICCodeEntity> codes = nicCodeService.getAllCodes();
        return new ResponseEntity<>(new ApiResponse<>("Codes fetched successfully", codes), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NICCodeEntity>> getCodeById(@PathVariable Long id) {
        NICCodeEntity code = nicCodeService.getCodeById(id);
        return new ResponseEntity<>(new ApiResponse<>("Code fetched successfully", code), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NICCodeEntity>> createCode(@RequestBody NICCodeEntity nicCodeEntity) {
        NICCodeEntity createdCode = nicCodeService.createCode(nicCodeEntity);
        return new ResponseEntity<>(new ApiResponse<>("Code created successfully", createdCode), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NICCodeEntity>> updateCode(@PathVariable Long id, @RequestBody NICCodeEntity nicCodeEntity) {
        NICCodeEntity updatedCode = nicCodeService.updateCode(id, nicCodeEntity);
        return new ResponseEntity<>(new ApiResponse<>("Code updated successfully", updatedCode), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCode(@PathVariable Long id) {
        nicCodeService.deleteCode(id);
        return new ResponseEntity<>(new ApiResponse<>("Code deleted successfully", null), HttpStatus.NO_CONTENT);
    }
}

