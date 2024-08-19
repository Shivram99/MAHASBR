package com.mahasbr.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.repository.TalukaMasterRepository;

import jakarta.transaction.Transactional;

@Service
public class TalukaMasterService {
	@Autowired
	TalukaMasterRepository talukaMasterRepository;

	@Transactional
	public Optional<TalukaMaster> getTalukaByDistrictCodeAndName(Long censusDistrictCode, String talukaName) {
		return talukaMasterRepository.findByCensusDistrictCodeAndTalukaName(censusDistrictCode, talukaName);
	}

	public Optional<TalukaMaster> findByCensusDistrictCodeAndTalukaName(String censusDistrictCode, String talukaName) {
		// TODO Auto-generated method stub
		return talukaMasterRepository.findByCensusDistrictCodeAndTalukaName(Long.parseLong(censusDistrictCode), talukaName);
	}

}
