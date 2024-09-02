package com.mahasbr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DistrictAndTimeWiseModel {

	 private String district;
	    private String quarter;
	    private Long count;
	    
	    
	    public DistrictAndTimeWiseModel(String district, String quarter, Long count) {
	        this.district = district;
	        this.quarter = quarter;
	        this.count = count;
	    }
	 
}
