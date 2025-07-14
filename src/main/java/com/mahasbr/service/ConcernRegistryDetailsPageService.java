package com.mahasbr.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mahasbr.entity.ConcernRegistryDetailsPageEntity;

public interface ConcernRegistryDetailsPageService {

	Page<ConcernRegistryDetailsPageEntity> getAllConcernRegistryDetails(Pageable pageable);

}
