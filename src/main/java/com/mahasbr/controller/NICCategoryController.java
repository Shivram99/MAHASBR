package com.mahasbr.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.dto.NICCategoryDTO;
import com.mahasbr.service.NICCategoryService;
import com.mahasbr.util.ApiResponse;
@RestController
@RequestMapping("/citizenSearch/api/nic-categories")
public class NICCategoryController {

	private static final Logger logger = LoggerFactory.getLogger(NICCategoryController.class);
	
    @Autowired
    private NICCategoryService nicCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NICCategoryDTO>>> getAllCategories() {
        List<NICCategoryDTO> categories = nicCategoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.ok(categories, "Categories fetched successfully"));
    }

    @GetMapping("/{categoryCode}")
    public ResponseEntity<ApiResponse<NICCategoryDTO>> getCategoryByCode(@PathVariable String categoryCode) {
        try {
            NICCategoryDTO category = nicCategoryService.getCategoryByCode(categoryCode);
            return ResponseEntity.ok(ApiResponse.ok(category, "Category fetched successfully"));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("Category not found: " + categoryCode));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NICCategoryDTO>> createCategory(@RequestBody NICCategoryDTO dto) {
        NICCategoryDTO createdCategory = nicCategoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(createdCategory, "Category created successfully"));
    }

    @PutMapping("/{categoryCode}")
    public ResponseEntity<ApiResponse<NICCategoryDTO>> updateCategory(
            @PathVariable String categoryCode,
            @RequestBody NICCategoryDTO dto) {

        NICCategoryDTO updatedCategory = nicCategoryService.updateCategory(categoryCode, dto);
        return ResponseEntity.ok(ApiResponse.ok(updatedCategory, "Category updated successfully"));
    }

    @PatchMapping("/{categoryCode}/toggle-status")
    public ResponseEntity<ApiResponse<NICCategoryDTO>> toggleCategoryStatus(@PathVariable String categoryCode) {
        NICCategoryDTO updated = nicCategoryService.toggleStatus(categoryCode);
        String msg = updated.getIsActive().equals("Y") ? "Category activated" : "Category deactivated";
        return ResponseEntity.ok(ApiResponse.ok(updated, msg));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            nicCategoryService.importFromExcel(file);
            return ResponseEntity.ok(ApiResponse.ok("Upload Successful", "Categories uploaded successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail("Upload failed: " + e.getMessage()));
        }
    }
}


