package com.mahasbr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.DetailsPage;
import com.mahasbr.entity.VillageMaster;
import com.mahasbr.repository.VillageMasterRepository;

@Service
public class VillageMasterService {
	@Autowired
	VillageMasterRepository villageMasterRepository;

	public List<VillageMaster> getVillageDtlByVillageName(DetailsPage details) {
		return villageMasterRepository.getVillageDtlByVillageName(details);
	}

	public Optional<VillageMaster> getVillagesByCensusTalukaCodeAndVillageName(Long censusTalukaCode,
			String villageName) {
		// TODO Auto-generated method stub
		return villageMasterRepository.getVillagesByCensusTalukaCodeAndVillageName(censusTalukaCode,villageName);
	}
	
	
	
	
	
	/*
	 * public VillageMaster insertVillageDetails(VillageMasterModel
	 * villageMasterModel) { VillageMaster data = new
	 * VillageMaster(villageMasterModel); villageMasterRepository.save(data); return
	 * data; }
	 */
}