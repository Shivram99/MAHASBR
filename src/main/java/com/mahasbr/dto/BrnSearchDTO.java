package com.mahasbr.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrnSearchDTO {

    private Long district;

    private String nameOfEstablishmentOrOwner;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Only alphanumeric characters are allowed.")
    @Size(min = 16, max = 16, message = "BRN number must be exactly 16 characters long.")
    private String brnNo;
}
