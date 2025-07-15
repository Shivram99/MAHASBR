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

import com.mahasbr.entity.NICCategoryEntity;
import com.mahasbr.service.NICCategoryService;
import com.mahasbr.util.ApiResponse;

@RestController
@RequestMapping("/api/nic-categories")
public class NICCategoryController {

    @Autowired
    private NICCategoryService nicCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NICCategoryEntity>>> getAllCategories() {
        List<NICCategoryEntity> categories = nicCategoryService.getAllCategories();
        return new ResponseEntity<>(new ApiResponse<>("Categories fetched successfully", categories), HttpStatus.OK);
    }

    @GetMapping("/{categoryCode}")
    public ResponseEntity<ApiResponse<NICCategoryEntity>> getCategoryByCode(@PathVariable String categoryCode) {
        NICCategoryEntity category = nicCategoryService.getCategoryByCode(categoryCode);
        return new ResponseEntity<>(new ApiResponse<>("Category fetched successfully", category), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NICCategoryEntity>> createCategory(@RequestBody NICCategoryEntity nicCategoryEntity) {
        NICCategoryEntity createdCategory = nicCategoryService.createCategory(nicCategoryEntity);
        return new ResponseEntity<>(new ApiResponse<>("Category created successfully", createdCategory), HttpStatus.CREATED);
    }

    @PutMapping("/{categoryCode}")
    public ResponseEntity<ApiResponse<NICCategoryEntity>> updateCategory(@PathVariable String categoryCode, @RequestBody NICCategoryEntity nicCategoryEntity) {
        NICCategoryEntity updatedCategory = nicCategoryService.updateCategory(categoryCode, nicCategoryEntity);
        return new ResponseEntity<>(new ApiResponse<>("Category updated successfully", updatedCategory), HttpStatus.OK);
    }

    @DeleteMapping("/{categoryCode}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable String categoryCode) {
        nicCategoryService.deleteCategory(categoryCode);
        return new ResponseEntity<>(new ApiResponse<>("Category deleted successfully", null), HttpStatus.NO_CONTENT);
    }
}
