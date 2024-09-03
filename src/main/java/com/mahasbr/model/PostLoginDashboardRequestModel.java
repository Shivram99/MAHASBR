package com.mahasbr.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostLoginDashboardRequestModel {

	private int page;
	private int size;
	private String sortBy;
	private List<Long> selectedDistrictIds;
	private List<Long> selectedTalukaIds;
	private FilterCriteria filters;

	
}
