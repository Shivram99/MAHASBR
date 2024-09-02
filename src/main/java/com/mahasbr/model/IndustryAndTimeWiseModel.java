package com.mahasbr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IndustryAndTimeWiseModel {

	 private String estateOwner;
	    private String quarter;
	    private Long count;

	    public IndustryAndTimeWiseModel(String estateOwner, String quarter, Long count) {
	        this.estateOwner = estateOwner;
	        this.quarter = quarter;
	        this.count = count;
	    }
}
