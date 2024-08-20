package com.mahasbr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.DetailsPage;
import com.mahasbr.repository.DetailsPageRepository;

@Service
public class DashboardServiceImpl implements DashboardService {
	@Autowired
	DetailsPageRepository detailsPageRepository;

	@Override
	public List<DetailsPage> getTimeAndIndustryWise() {
		return detailsPageRepository.getTimeAndIndustryWise();

	}

	@Override
	public List<DetailsPage> getIndustryWiseStats() {
		String maxDate = detailsPageRepository.findMaxDate();
		return detailsPageRepository.getIndustryWiseStats(maxDate);

	}

	@Override
	public List<DetailsPage> getActandtimeWiseStats() {

		return detailsPageRepository.findActAndYearCounts();
	}

	@Override
	public List<DetailsPage> getDistrictAndActWiseStatsForLatestYear() {
		// TODO Auto-generated method stub
		return detailsPageRepository.findDistrictAndActWiseForLatestYear();
	}

	@Override
	public List<DetailsPage> getDistricrtAndTimeWise() {
		return detailsPageRepository.getDistricrtAndTimeWise();
	}
   
	
}
