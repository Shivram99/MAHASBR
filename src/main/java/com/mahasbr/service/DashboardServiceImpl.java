package com.mahasbr.service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.model.ActAndYearWiseModel;
import com.mahasbr.model.DistrictAndActWiseModel;
import com.mahasbr.model.DistrictAndIndustryModel;
import com.mahasbr.model.DistrictAndTimeWiseModel;
import com.mahasbr.model.IndustryAndTimeWiseModel;
import com.mahasbr.repository.MstRegistryDetailsPageRepository;

@Service
public class DashboardServiceImpl implements DashboardService {
	@Autowired
	MstRegistryDetailsPageRepository mstRegistryDetailsPageRepository;

	@Override
	public List<DistrictAndTimeWiseModel> getDistrictAndTimeWise(String district, String quarter, String operation) {
		// Fetch all records from the repository
		List<MstRegistryDetailsPageEntity> allRecords = mstRegistryDetailsPageRepository.findAll();

	    // Filter records based on the provided district, quarter, and operation
	    Map<String, Long> filteredData = allRecords.stream()
	            .filter(record -> record.getDistrict().equalsIgnoreCase(district))
	            .filter(record -> {
	                switch (operation) {
	                    case "Total Registration (TR)":
	                        return getFinancialQuarter(record.getDateOfRegistration()).equalsIgnoreCase(quarter);
	                    case "Deregistration (DR)":
	                        return getFinancialQuarter(record.getDateOfDeregistrationExpiry()).equalsIgnoreCase(quarter);
	                    case "Count of New Registration (NR)":
	                        return getFinancialQuarter(record.getDateOfRegistration()).equalsIgnoreCase(quarter);
	                    default:
	                        return false;
	                }
	            })
	            .collect(Collectors.groupingBy(
	                MstRegistryDetailsPageEntity::getDistrict, // Group by district
	                Collectors.counting() // Count records in the filtered group
	            ));

	    // Convert the filtered data into a list of DistrictAndTimeWiseModel
	    return filteredData.entrySet().stream()
	            .map(entry -> new DistrictAndTimeWiseModel(
	                entry.getKey(), // District
	                quarter, // Quarter
	                entry.getValue() // Count
	            ))
	            .collect(Collectors.toList());
	}

	@Override
	public List<ActAndYearWiseModel> getActAndYearWise(String act, String year, String operation) {
		// Fetch all records from the repository
		 List<MstRegistryDetailsPageEntity> allRecords = mstRegistryDetailsPageRepository.findAll();

		    // Filter records by act and year based on the operation
		    Map<String, Long> filteredData = allRecords.stream()
		            .filter(record -> record.getNameOfAct().equalsIgnoreCase(act))
		            .filter(record -> {
		                switch (operation) {
		                    case "Total Registration (TR)":
		                        return getYear(record.getDateOfRegistration()).equals(year);
		                    case "Deregistration (DR)":
		                        return getYear(record.getDateOfDeregistrationExpiry()).equals(year);
		                    case "Count of New Registration (NR)":
		                        return getYear(record.getDateOfRegistration()).equals(year);
		                    default:
		                        return false;
		                }
		            })
		            .collect(Collectors.groupingBy(
		                MstRegistryDetailsPageEntity::getNameOfAct, // Group by act
		                Collectors.counting() // Count records in each group
		            ));

		    // Convert the filtered data into a list of ActAndYearWiseModel
		    return filteredData.entrySet().stream()
		            .map(entry -> new ActAndYearWiseModel(
		                entry.getKey(), // Act
		                year, // Year
		                entry.getValue() // Count
		            ))
		            .collect(Collectors.toList());
	}

	private String getYear(LocalDate date) {
		return String.valueOf(date.getYear()); // Convert year to String
	}

	@Override
	public List<DistrictAndActWiseModel> getDistrictAndActWise(String district, String act, String operation) {
		// Fetch all records from the repository
		List<MstRegistryDetailsPageEntity> allRecords = mstRegistryDetailsPageRepository.findAll();

		// Filter records by district and act
		Map<String, Long> filteredData = allRecords.stream()
				.filter(record -> record.getDistrict().equalsIgnoreCase(district))
				.filter(record -> record.getNameOfAct().equalsIgnoreCase(act))
				.collect(Collectors.groupingBy(record -> record.getDistrict() + "-" + record.getNameOfAct(), // Group by district and act
						Collectors.counting() // Count records in each group
				));

		// Convert the filtered data into a list of DistrictAndActWiseModel
		return filteredData.entrySet().stream().map(entry -> {
			String[] keyParts = entry.getKey().split("-");
			return new DistrictAndActWiseModel(keyParts[0], // District
					keyParts[1], // Act
					entry.getValue() // Count
			);
		}).collect(Collectors.toList());
	}

	private String getFinancialQuarter(LocalDate date) {
        Month month = date.getMonth();

        if (month == Month.JANUARY || month == Month.FEBRUARY || month == Month.MARCH) {
            return "Q1";
        } else if (month == Month.APRIL || month == Month.MAY || month == Month.JUNE) {
            return "Q2";
        } else if (month == Month.JULY || month == Month.AUGUST || month == Month.SEPTEMBER) {
            return "Q3";
        } else {
            return "Q4";
        }
	}

	@Override
	public List<DistrictAndIndustryModel> getDistrictAndEstateOwnerWise(String district, String estateOwner, String operation) {
		// Fetch all records from the repository
		List<MstRegistryDetailsPageEntity> allRecords = mstRegistryDetailsPageRepository.findAll();

		// Filter and group the data by district and NAME_OF_ESTATE_OWNER
		Map<String, Map<String, Long>> groupedData = allRecords.stream()
				.filter(record -> record.getDistrict().equalsIgnoreCase(district))
				.filter(record -> record.getNameOfEstablishmentOrOwner().equalsIgnoreCase(estateOwner))
				.collect(Collectors.groupingBy(MstRegistryDetailsPageEntity::getDistrict, // Group by district
						Collectors.groupingBy(MstRegistryDetailsPageEntity::getNameOfEstablishmentOrOwner, // Then group by estate owner
								Collectors.counting() // Count records in each group
						)));

		// Convert the grouped data into a list of DistrictAndIndustryModel
		List<DistrictAndIndustryModel> result = new ArrayList<>();
		for (Map.Entry<String, Map<String, Long>> districtEntry : groupedData.entrySet()) {
			String districtKey = districtEntry.getKey();
			Map<String, Long> estateOwners = districtEntry.getValue();

			for (Map.Entry<String, Long> estateOwnerEntry : estateOwners.entrySet()) {
				String estateOwnerKey = estateOwnerEntry.getKey();
				Long count = estateOwnerEntry.getValue();

				result.add(new DistrictAndIndustryModel(districtKey, estateOwnerKey, count));
			}
		}

		return result;
	}

	@Override
	public List<IndustryAndTimeWiseModel> getEstateOwnerAndQuarterWise(String estateOwner, String quarter, String operation) {
		// Fetch all records from the repository
		List<MstRegistryDetailsPageEntity> allRecords = mstRegistryDetailsPageRepository.findAll();

        // Filter and group the data by NAME_OF_ESTATE_OWNER and financial quarter
        Map<String, Long> filteredData = allRecords.stream()
                .filter(record -> record.getNameOfEstablishmentOrOwner().equalsIgnoreCase(estateOwner))
                .filter(record -> getFinancialQuarter(record.getDateOfRegistration()).equalsIgnoreCase(quarter)) // Assuming the quarter is derived from registration date
                .collect(Collectors.groupingBy(MstRegistryDetailsPageEntity::getNameOfEstablishmentOrOwner, 
                        Collectors.counting() 
                ));

        // Convert the filtered data into a list of IndustryAndTimeWiseModel
        return filteredData.entrySet().stream()
                .map(entry -> new IndustryAndTimeWiseModel(entry.getKey(), quarter, entry.getValue()))
                .collect(Collectors.toList());
    }
}
