package com.mahasbr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.entity.VillageMaster;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.repository.TalukaMasterRepository;
import com.mahasbr.repository.VillageMasterRepository;
import com.mahasbr.service.StatesMasterService;

@Component
public class LocationGenerator {
	
	@Autowired
	StatesMasterService statesMasterService;
	
	@Autowired
	DistrictMasterRepository districtMasterRepository;
	
	@Autowired
	TalukaMasterRepository talukaMasterRepository;
	
	@Autowired
	VillageMasterRepository villageMasterRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(LocationGenerator.class);
	
	
	public String getLocationCode(String districtName, String talukaName, String villageName) {

		// Normalize inputs
		districtName = StringUtils.trimToEmpty(districtName);
		talukaName = StringUtils.trimToEmpty(talukaName);
		villageName = StringUtils.trimToEmpty(villageName);

		// Validate required inputs
		List<String> missingFields = new ArrayList<>();
		if (districtName.isEmpty())
			missingFields.add("DISTRICT");
		if (talukaName.isEmpty())
			missingFields.add("TALUKA");
		if (villageName.isEmpty())
			missingFields.add("VILLAGE");

		if (!missingFields.isEmpty()) {
			logger.warn("Missing mandatory location fields: {}", missingFields);
			return "NA";
		}

		try {
			// Step 1 → Get Census District Code
			Optional<String> districtCodeOpt = districtMasterRepository
					.findCensusDistrictCodeByNameAndState(districtName, "27");

			if (districtCodeOpt.isEmpty()) {
				logger.warn("District not found: {}", districtName);
				return "NA";
			}

			String districtCode = districtCodeOpt.get();

			// Step 2 → Get Taluka
			Optional<TalukaMaster> talukaOpt = talukaMasterRepository
					.findByDistrictCodeAndTalukaNameIgnoreCase(districtCode, talukaName);

			if (talukaOpt.isEmpty()) {
				logger.warn("Taluka not found for district={}, taluka={}", districtCode, talukaName);
				return "NA";
			}

			TalukaMaster taluka = talukaOpt.get();

			// Step 3 → Get Village
			Optional<VillageMaster> villageOpt = villageMasterRepository
					.findByCensusDistrictCodeAndCensusTalukaCodeAndVillageNameIgnoreCase(districtCode,
							taluka.getCensusTalukaCode(), villageName);

			if (villageOpt.isEmpty()) {
				logger.warn("Village not found for district={}, taluka={}, village={}", districtCode,
						taluka.getCensusTalukaCode(), villageName);
				return "NA";
			}

			VillageMaster village = villageOpt.get();

			// Step 4 → Build Final Location Code
			return "27"+districtCode + taluka.getCensusTalukaCode() + village.getCensusVillageCode();

		} catch (Exception ex) {
			logger.error("Unexpected error generating location code", ex);
			return "NA";
		}
	}
	
	
}


