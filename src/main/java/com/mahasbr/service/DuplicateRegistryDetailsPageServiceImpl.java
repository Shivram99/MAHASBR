package com.mahasbr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.DuplicateRegistryDetailsPageEntity;
import com.mahasbr.repository.DuplicateRegistryDetailsPageRepository;

@Service
public class DuplicateRegistryDetailsPageServiceImpl implements DuplicateRegistryDetailsPageService {
	
	@Autowired
	DuplicateRegistryDetailsPageRepository DuplicateRegistryDetailsPageRepository;

	@Override
	public Page<DuplicateRegistryDetailsPageEntity> getAllDuplicateRegistryDetails(Pageable pageable) {
		// TODO Auto-generated method stub
		return DuplicateRegistryDetailsPageRepository.findAll(pageable);
	}
}
