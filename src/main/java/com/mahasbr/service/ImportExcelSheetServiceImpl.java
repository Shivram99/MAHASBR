package com.mahasbr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.DetailsPage;
import com.mahasbr.repository.DetailsPageRepository;

@Service
public class ImportExcelSheetServiceImpl implements ImportExcelSheetService {
	
	
	@Autowired
	DetailsPageRepository detailsPageRepository;

	@Override
	public void save(DetailsPage details) {
		detailsPageRepository.save(details);				
	}

	@Override
	public DetailsPage findOrgData(DetailsPage details) {
		return detailsPageRepository.getDetailsByColumn(details.getNameOfEstateOwner(),details.getHouseNo(), details.getStreetName(), details.getLocality(), details.getTownVillage(),
				details.getTaluka(),details.getDistrict(), details.getSector(), details.getNameofAuth(),
				details.getNameofAct());
	}
	

	
}




