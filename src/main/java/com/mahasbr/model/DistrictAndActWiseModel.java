package com.mahasbr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DistrictAndActWiseModel {

	  private String district;
	    private String act;
	    private Long count;

	    public DistrictAndActWiseModel(String district, String act, Long count) {
	        this.district = district;
	        this.act = act;
	        this.count = count;
	    }
	}

