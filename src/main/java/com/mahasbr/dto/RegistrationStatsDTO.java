package com.mahasbr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationStatsDTO {
	private String district;
    private int year;
    private String quarter;
    private long totalRegistrations;

}
