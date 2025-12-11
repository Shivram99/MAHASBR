package com.mahasbr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class NICDivisionDTO {
	    private String divisionCode;
	    private String description;
	    private String categoryCode;
	    private String isActive;
}
