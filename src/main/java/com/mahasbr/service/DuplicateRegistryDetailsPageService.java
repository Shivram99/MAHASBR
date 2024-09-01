package com.mahasbr.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mahasbr.entity.DuplicateRegistryDetailsPageEntity;

public interface DuplicateRegistryDetailsPageService {

	Page<DuplicateRegistryDetailsPageEntity> getAllDuplicateRegistryDetails(Pageable pageable);

}
