package com.mahasbr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.ConcernRegistryDetailsPageEntity;
import com.mahasbr.repository.ConcernRegistryDetailsPageRepository;

@Service
public class ConcernRegistryDetailsPageServiceImpl implements ConcernRegistryDetailsPageService {
	
	@Autowired
	ConcernRegistryDetailsPageRepository concernRegistryDetailsPageRepository;

	@Override
	public Page<ConcernRegistryDetailsPageEntity> getAllConcernRegistryDetails(Pageable pageable) {
		return concernRegistryDetailsPageRepository.findAll(pageable);
	}
	
}
