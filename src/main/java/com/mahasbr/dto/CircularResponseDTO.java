package com.mahasbr.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircularResponseDTO {
    private Long id;
    private String subject;
    private LocalDate date;
    private String fileUrl;
    private String message;
    private String status;
}
