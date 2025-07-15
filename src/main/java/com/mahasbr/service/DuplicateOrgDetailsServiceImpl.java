package com.mahasbr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.DuplicateOrgDetailsEntity;
import com.mahasbr.entity.DuplicateRegistryDetailsPageEntity;
import com.mahasbr.repository.DuplicateOrgDetailsRepository;

@Service
public class DuplicateOrgDetailsServiceImpl implements DuplicateOrgDetailsService{
	
	@Autowired
	DuplicateOrgDetailsRepository duplicateOrgDetailsRepository;

	@Override
	public void save(DuplicateOrgDetailsEntity duplicateOrgDetailsEntity) {
		duplicateOrgDetailsRepository.save(duplicateOrgDetailsEntity);
	}

	public Page<DuplicateRegistryDetailsPageEntity> getAllDuplicateRegistryDetails(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

}
