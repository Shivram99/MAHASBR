package com.mahasbr.dto;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CircularRequestDTO {
	private Long id;
    private String subject;
    private LocalDate date;
    private MultipartFile file; // PDF file
}