package com.mahasbr.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.dto.CitizenDashboardData;
import com.mahasbr.dto.CitizenDashboardDataRegDeRegNewReg;
import com.mahasbr.dto.RegistrationStatsDTO;
import com.mahasbr.repository.RegistrationRepository;

@Service
public class RegistrationService {

	@Autowired
	private RegistrationRepository repository;

	public List<RegistrationStatsDTO> getRegistrationStats() {
		List<Object[]> results = repository.getRegistrationStatsRaw();

		return results.stream().map(r -> new RegistrationStatsDTO((String) r[0], ((Number) r[1]).intValue(),
				(String) r[2], ((Number) r[3]).longValue())).collect(Collectors.toList());
	}

	public List<CitizenDashboardData> citizenDashboardDataNR() {
		List<CitizenDashboardData> citizenDashboardData = repository.citizenDashboardDataNR();
		for (CitizenDashboardData data : citizenDashboardData) {
		    System.out.println("Registry Name: " + data.getRegistryName());
		    System.out.println("District: " + data.getDistrict());
		    System.out.println("Division: " + data.getDivision());
		    System.out.println("Year: " + data.getYear());
		    System.out.println("Quarter: " + data.getQuarter());
		    System.out.println("Total Registrations: " + data.getTotalRegistrations());
		    System.out.println("Total Persons Working: " + data.getTOTALPERSONSWORKING());
		    System.out.println("----------------------------------------");
		}
		return citizenDashboardData;
	}
	
	public List<CitizenDashboardData> citizenDashboardDataTR() {
		List<CitizenDashboardData> citizenDashboardData = repository.citizenDashboardDataTR();
		return citizenDashboardData;
	}
	
	public List<CitizenDashboardData> citizenDashboardDataDR() {
		List<CitizenDashboardData> citizenDashboardData = repository.citizenDashboardDataDR();

		return citizenDashboardData;
	}
	public List<CitizenDashboardDataRegDeRegNewReg> citizenDashboardDataRegDeRegNewReg() {
		List<CitizenDashboardDataRegDeRegNewReg> citizenDashboardData = repository.citizenDashboardDataRegDeRegNewReg();
		
		return citizenDashboardData;
	}
}
