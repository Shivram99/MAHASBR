package com.mahasbr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DistrictAndIndustryModel {

	 private String district;
	    private String estateOwner;
	    private Long count;

	    public DistrictAndIndustryModel(String district, String estateOwner, Long count) {
	        this.district = district;
	        this.estateOwner = estateOwner;
	        this.count = count;
	    }
}
