package com.mahasbr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.mahasbr.dto.NICClassDTO;
import com.mahasbr.service.NICClassService;
import com.mahasbr.util.ApiResponse;
@RestController
@RequestMapping("/api/nic-classes")
public class NICClassController {

    @Autowired
    private NICClassService nicClassService;

    /** ✅ Fetch all classes (DTO List) */
    @GetMapping
    public ResponseEntity<ApiResponse<List<NICClassDTO>>> getAllClasses() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Classes fetched successfully", nicClassService.getAllClasses())
        );
    }

    /** ✅ Fetch classes by group for filtering */
    @GetMapping("/by-group/{groupCode}")
    public ResponseEntity<ApiResponse<List<NICClassDTO>>> getClassesByGroup(@PathVariable String groupCode) {
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Classes fetched for group: " + groupCode,
                        nicClassService.getClassesByGroup(groupCode))
        );
    }

    /** ✅ Get class by code (DTO) */
    @GetMapping("/{classCode}")
    public ResponseEntity<ApiResponse<NICClassDTO>> getClassByCode(@PathVariable String classCode) {

        NICClassDTO dto = nicClassService.getClassByCode(classCode);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Class fetched successfully", dto)
        );
    }

    /** ✅ Create Class */
    @PostMapping
    public ResponseEntity<ApiResponse<NICClassDTO>> createClass(@RequestBody NICClassDTO dto) {

        NICClassDTO created = nicClassService.createClass(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Class created successfully", created));
    }

    /** ✅ Update Existing Class */
    @PutMapping("/{classCode}")
    public ResponseEntity<ApiResponse<NICClassDTO>> updateClass(
            @PathVariable String classCode,
            @RequestBody NICClassDTO dto) {

        NICClassDTO updated = nicClassService.updateClass(classCode, dto);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Class updated successfully", updated)
        );
    }

    /** ✅ Toggle Active / Inactive */
    @PatchMapping("/{classCode}/toggle-status")
    public ResponseEntity<ApiResponse<NICClassDTO>> toggleStatus(@PathVariable String classCode) {

        NICClassDTO updated = nicClassService.toggleStatus(classCode);
        String msg = updated.getIsActive().equals("Y")
                ? "Class activated"
                : "Class deactivated";

        return ResponseEntity.ok(
                new ApiResponse<>(true, msg, updated)
        );
    }

    /** ❌ Hard delete (not recommended but added for completeness) */
    @DeleteMapping("/{classCode}")
    public ResponseEntity<ApiResponse<Void>> deleteClass(@PathVariable String classCode) {

        nicClassService.deleteClass(classCode);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new ApiResponse<>(true, "Class deleted successfully", null));
    }
}

