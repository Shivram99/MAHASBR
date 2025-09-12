package com.mahasbr.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.mahasbr.api.dto.FactoryData;
import com.mahasbr.api.dto.LoginRequest;
import com.mahasbr.api.dto.ShopData;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LabourExternalApiService {

	private final WebClient webClient;

	public LabourExternalApiService(WebClient.Builder builder) {
		// Base URL for Labour Management System (SBR LMS) API
		this.webClient = builder.baseUrl("https://sbrlmsapi.mahaitgov.in/api").build();
	}

	/**
	 * Step 1: Authenticate with the external LMS API. Returns a JWT token required
	 * for subsequent API calls.
	 */
	public Mono<String> authenticate() {
		return webClient.post().uri("/Login").bodyValue(new LoginRequest("axe2Zf3HlSBr5L6", "7FVaTuMIarLMAso"))
				.retrieve().bodyToMono(String.class).map(token -> token.replace("\"", "")); // remove quotes
	}

	/**
	 * Step 2: Fetch Shop Data. Requires Authorization token from authenticate().
	 */
	public Flux<ShopData> getShopData(String token) {
		return webClient.post().uri("/SBR/GetSBRShopData").header("Authorization", "Bearer " + token).retrieve()
				.bodyToFlux(ShopData.class);
	}

	/**
	 * Step 3: Fetch Factory Data. Requires Authorization token from authenticate().
	 */
	public Flux<FactoryData> getFactoryData(String token) {
		return webClient.post().uri("/SBR/getSBRFactoryData").header("Authorization", "Bearer " + token).retrieve()
				.bodyToFlux(FactoryData.class);
	}

	/**
	 * Combined method: Authenticate first, then fetch both Shops and Factories.
	 * Returns summary string with counts.
	 */
	public Mono<String> getAllData() {
		return authenticate().flatMap(token -> getShopData(token).collectList()
				.zipWith(getFactoryData(token).collectList(), (shops, factories) -> "Shops: " + shops.size()
						+ " entries\n" + "Factories: " + factories.size() + " entries"));
	}
}
