package com.mahasbr.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.dto.CircularRequestDTO;
import com.mahasbr.dto.CircularResponseDTO;
import com.mahasbr.service.CircularService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/circulars")
@RequiredArgsConstructor
public class CircularController {

    private final CircularService circularService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CircularResponseDTO> createCircular(
            @RequestParam String subject,
            @RequestParam(required = false) String activity,
            @RequestParam String date,
            @RequestParam MultipartFile file) {

        CircularRequestDTO dto = CircularRequestDTO.builder()
                .subject(subject)
                .activity(activity)
                .date(LocalDate.parse(date))
                .file(file)
                .build();

        return ResponseEntity.ok(circularService.createCircular(dto));
    }
    
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CircularResponseDTO> updateCircular(
    		@RequestParam Long id,
            @RequestParam String subject,
            @RequestParam(required = false) String activity,
            @RequestParam String date,
            @RequestParam(required = false) MultipartFile file) {

        CircularRequestDTO dto = CircularRequestDTO.builder()
        		.id(id)
                .subject(subject)
                .activity(activity)
                .date(LocalDate.parse(date))
                .file(file)
                .build();

        return ResponseEntity.ok(circularService.updateCircular(dto));
    }

    @GetMapping
    public ResponseEntity<List<CircularResponseDTO>> getAllCirculars() {
        return ResponseEntity.ok(circularService.getAllCirculars());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CircularResponseDTO> getCircular(@PathVariable Long id) {
        return ResponseEntity.ok(circularService.getCircularById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCircular(@PathVariable Long id) {
        circularService.deleteCircular(id);
        return ResponseEntity.noContent().build();
    }
}
