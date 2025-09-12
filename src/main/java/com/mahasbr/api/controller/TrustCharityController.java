package com.mahasbr.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.api.service.TrustListService;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;

import reactor.core.publisher.Flux;

@RestController
public class TrustCharityController {

	// Service to fetch Trust / Charity related data
	private final TrustListService trustListService;

	public TrustCharityController(TrustListService trustListService) {
		this.trustListService = trustListService;
	}

	/**
	 * API endpoint to fetch list of registered Trusts / Charities. This data comes
	 * from the external system integrated with the Labour/Charity department.
	 *
	 * @return Flux<TrustListResponseDTO> - Stream of Trust/Charity details
	 */
	/*
	 * @GetMapping("/api/trustlist") public Flux<MstRegistryDetailsPageEntity>
	 * fetchTrustList() { return trustListService.getTrustList(); }
	 */
}
