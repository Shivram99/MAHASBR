package com.mahasbr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.DuplicateOrgDetailsEntity;
import com.mahasbr.repository.DuplicateOrgDetailsRepository;

@Service
public class DuplicateOrgDetailsServiceImpl implements DuplicateOrgDetailsService{
	
	@Autowired
	DuplicateOrgDetailsRepository duplicateOrgDetailsRepository;

	@Override
	public void save(DuplicateOrgDetailsEntity duplicateOrgDetailsEntity) {
		duplicateOrgDetailsRepository.save(duplicateOrgDetailsEntity);
	}

}
