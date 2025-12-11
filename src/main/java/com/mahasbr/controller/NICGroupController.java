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

import com.mahasbr.dto.NICGroupDTO;
import com.mahasbr.entity.NICGroupEntity;
import com.mahasbr.service.NICGroupService;
import com.mahasbr.util.ApiResponse;
@RestController
@RequestMapping("/citizenSearch/api/nic-groups")
public class NICGroupController {

    @Autowired
    private NICGroupService groupService;

    // ---------------------------------------------------------
    // GET ALL GROUPS
    // ---------------------------------------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<List<NICGroupDTO>>> getAllGroups() {
        return ResponseEntity.ok(
                ApiResponse.ok(groupService.getAllGroups(), "Groups fetched successfully")
        );
    }

    // ---------------------------------------------------------
    // GET GROUPS BY DIVISION
    // ---------------------------------------------------------
    @GetMapping("/by-division/{divisionCode}")
    public ResponseEntity<ApiResponse<List<NICGroupDTO>>> getGroupsByDivision(
            @PathVariable String divisionCode) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        groupService.getGroupsByDivision(divisionCode),
                        "Groups fetched for division: " + divisionCode
                )
        );
    }

    // ---------------------------------------------------------
    // GET SINGLE GROUP
    // ---------------------------------------------------------
    @GetMapping("/{groupCode}")
    public ResponseEntity<ApiResponse<NICGroupDTO>> getGroupByCode(@PathVariable String groupCode) {

        NICGroupDTO dto = groupService.getGroupByCode(groupCode);
        return ResponseEntity.ok(
                ApiResponse.ok(dto, "Group fetched successfully")
        );
    }

    // ---------------------------------------------------------
    // CREATE GROUP  (return DTO, not entity)
    // ---------------------------------------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<NICGroupDTO>> createGroup(@RequestBody NICGroupDTO dto) {

        NICGroupEntity saved = groupService.createGroup(dto);
        NICGroupDTO responseDto = groupService.getGroupByCode(saved.getGroupCode());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(responseDto, "Group created successfully"));
    }

    // ---------------------------------------------------------
    // UPDATE GROUP (return DTO, not entity)
    // ---------------------------------------------------------
    @PutMapping("/{groupCode}")
    public ResponseEntity<ApiResponse<NICGroupDTO>> updateGroup(
            @PathVariable String groupCode,
            @RequestBody NICGroupDTO dto) {

        groupService.updateGroup(groupCode, dto);
        NICGroupDTO responseDto = groupService.getGroupByCode(groupCode);

        return ResponseEntity.ok(
                ApiResponse.ok(responseDto, "Group updated successfully")
        );
    }

    // ---------------------------------------------------------
    // TOGGLE ACTIVE / INACTIVE
    // ---------------------------------------------------------
    @PatchMapping("/{groupCode}/toggle-status")
    public ResponseEntity<ApiResponse<NICGroupDTO>> toggleStatus(@PathVariable String groupCode) {

        groupService.toggleStatus(groupCode);
        NICGroupDTO dto = groupService.getGroupByCode(groupCode);

        String msg = dto.getIsActive().equals("Y")
                ? "Group activated"
                : "Group deactivated";

        return ResponseEntity.ok(
                ApiResponse.ok(dto, msg)
        );
    }

    // ---------------------------------------------------------
    // DELETE GROUP (HARD DELETE)
    // ---------------------------------------------------------
    @DeleteMapping("/{groupCode}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(@PathVariable String groupCode) {

        groupService.deleteGroup(groupCode);

        return ResponseEntity.ok(
                ApiResponse.ok(null, "Group deleted successfully")
        );
    }
}

