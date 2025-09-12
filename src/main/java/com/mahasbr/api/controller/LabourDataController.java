package com.mahasbr.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.api.service.LabourExternalApiService;

import reactor.core.publisher.Mono;

@RestController
public class LabourDataController {

	private final LabourExternalApiService labourExternalApiService;

	public LabourDataController(LabourExternalApiService labourExternalApiService) {
		this.labourExternalApiService = labourExternalApiService;
	}

	/**
	 * API endpoint to fetch list of Shops. This will authenticate first with the
	 * external system and then fetch shop data registered with the Labour
	 * Department.
	 *
	 * @return List of Shops (as JSON)
	 */
	@GetMapping("/shops")
	public Mono<?> getShops() {
		return labourExternalApiService.authenticate().flatMapMany(labourExternalApiService::getShopData).collectList();
	}

	/**
	 * API endpoint to fetch list of Factories. This will authenticate first with
	 * the external system and then fetch factory data registered with the Labour
	 * Department.
	 *
	 * @return List of Factories (as JSON)
	 */
	@GetMapping("/factories")
	public Mono<?> getFactories() {
		return labourExternalApiService.authenticate().flatMapMany(labourExternalApiService::getFactoryData)
				.collectList();
	}

	/**
	 * API endpoint to fetch combined summary of Shops and Factories. Instead of
	 * returning complete data, it provides count of Shops and Factories available
	 * in the external system.
	 *
	 * @return String response with Shops count and Factories count
	 */
	@GetMapping("/allData")
	public Mono<String> getAllData() {
		return labourExternalApiService.getAllData();
	}
}
