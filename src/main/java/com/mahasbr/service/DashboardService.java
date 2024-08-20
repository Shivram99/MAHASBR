package com.mahasbr.service;

import java.util.List;

import com.mahasbr.entity.DetailsPage;

public interface DashboardService {

	List<DetailsPage> getTimeAndIndustryWise();

	List<DetailsPage> getIndustryWiseStats();

	List<DetailsPage> getActandtimeWiseStats();

	List<DetailsPage> getDistrictAndActWiseStatsForLatestYear();
	
	List<DetailsPage> getDistricrtAndTimeWise();

}
