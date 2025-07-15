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

import com.mahasbr.entity.NICGroupEntity;
import com.mahasbr.service.NICGroupService;
import com.mahasbr.util.ApiResponse;

@RestController
@RequestMapping("/api/nic-groups")
public class NICGroupController {

    @Autowired
    private NICGroupService nicGroupService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NICGroupEntity>>> getAllGroups() {
        List<NICGroupEntity> groups = nicGroupService.getAllGroups();
        return new ResponseEntity<>(new ApiResponse<>("Groups fetched successfully", groups), HttpStatus.OK);
    }

    @GetMapping("/{groupCode}")
    public ResponseEntity<ApiResponse<NICGroupEntity>> getGroupByCode(@PathVariable String groupCode) {
        NICGroupEntity group = nicGroupService.getGroupByCode(groupCode);
        return new ResponseEntity<>(new ApiResponse<>("Group fetched successfully", group), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NICGroupEntity>> createGroup(@RequestBody NICGroupEntity nicGroupEntity) {
        NICGroupEntity createdGroup = nicGroupService.createGroup(nicGroupEntity);
        return new ResponseEntity<>(new ApiResponse<>("Group created successfully", createdGroup), HttpStatus.CREATED);
    }

    @PutMapping("/{groupCode}")
    public ResponseEntity<ApiResponse<NICGroupEntity>> updateGroup(@PathVariable String groupCode, @RequestBody NICGroupEntity nicGroupEntity) {
        NICGroupEntity updatedGroup = nicGroupService.updateGroup(groupCode, nicGroupEntity);
        return new ResponseEntity<>(new ApiResponse<>("Group updated successfully", updatedGroup), HttpStatus.OK);
    }

    @DeleteMapping("/{groupCode}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(@PathVariable String groupCode) {
        nicGroupService.deleteGroup(groupCode);
        return new ResponseEntity<>(new ApiResponse<>("Group deleted successfully", null), HttpStatus.NO_CONTENT);
    }
}
