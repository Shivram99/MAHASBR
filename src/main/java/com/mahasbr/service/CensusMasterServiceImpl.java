package com.mahasbr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.repository.CensusEntityRepository;


@Service
public class CensusMasterServiceImpl implements CensusMasterService {
	
	@Autowired
	CensusEntityRepository censusEntityRepository;
	
	
	
	
	

}
