package com.mahasbr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActAndYearWiseModel {
	
	 private String act;
	    private String year;
	    private Long count;

	    public ActAndYearWiseModel(String act, String year, Long count) {
	        this.act = act;
	        this.year = year;
	        this.count = count;
	    }
	}
