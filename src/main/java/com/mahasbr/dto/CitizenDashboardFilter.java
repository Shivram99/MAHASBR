package com.mahasbr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CitizenDashboardFilter {
	private String countType;
    private String act;
    private String region;
    private String district;
    private String year;
    private String quarter;
    private String nic;
}
