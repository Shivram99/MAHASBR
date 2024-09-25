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

import com.mahasbr.entity.NICClassEntity;
import com.mahasbr.service.NICClassService;
import com.mahasbr.util.ApiResponse;

@RestController
@RequestMapping("/api/nic-classes")
public class NICClassController {

    @Autowired
    private NICClassService nicClassService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NICClassEntity>>> getAllClasses() {
        List<NICClassEntity> classes = nicClassService.getAllClasses();
        return new ResponseEntity<>(new ApiResponse<>("Classes fetched successfully", classes), HttpStatus.OK);
    }

    @GetMapping("/{classCode}")
    public ResponseEntity<ApiResponse<NICClassEntity>> getClassByCode(@PathVariable String classCode) {
        NICClassEntity nicClass = nicClassService.getClassByCode(classCode);
        return new ResponseEntity<>(new ApiResponse<>("Class fetched successfully", nicClass), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NICClassEntity>> createClass(@RequestBody NICClassEntity nicClassEntity) {
        NICClassEntity createdClass = nicClassService.createClass(nicClassEntity);
        return new ResponseEntity<>(new ApiResponse<>("Class created successfully", createdClass), HttpStatus.CREATED);
    }

    @PutMapping("/{classCode}")
    public ResponseEntity<ApiResponse<NICClassEntity>> updateClass(@PathVariable String classCode, @RequestBody NICClassEntity nicClassEntity) {
        NICClassEntity updatedClass = nicClassService.updateClass(classCode, nicClassEntity);
        return new ResponseEntity<>(new ApiResponse<>("Class updated successfully", updatedClass), HttpStatus.OK);
    }

    @DeleteMapping("/{classCode}")
    public ResponseEntity<ApiResponse<Void>> deleteClass(@PathVariable String classCode) {
        nicClassService.deleteClass(classCode);
        return new ResponseEntity<>(new ApiResponse<>("Class deleted successfully", null), HttpStatus.NO_CONTENT);
    }
}
