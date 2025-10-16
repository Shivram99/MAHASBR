package com.mahasbr.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/api/circulars")
public class CircularController {

	private static final Logger logger = LoggerFactory.getLogger(CircularController.class);

	@Autowired
    private CircularService circularService;

    @PostMapping
    public ResponseEntity<CircularResponseDTO> createCircular(
            @RequestParam String subject,
            @RequestParam String date,
            @RequestParam MultipartFile file) {

        CircularRequestDTO dto = CircularRequestDTO.builder()
                .subject(subject)
                .date(LocalDate.parse(date))
                .file(file)
                .build();

        return ResponseEntity.ok(circularService.createCircular(dto));
    }
    
    @PutMapping
    public ResponseEntity<CircularResponseDTO> UpdateCircular(
    		@RequestParam Long id,
            @RequestParam String subject,
            @RequestParam String date,
            @RequestParam MultipartFile file) {

        CircularRequestDTO dto = CircularRequestDTO.builder()
        		.id(id)
                .subject(subject)
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