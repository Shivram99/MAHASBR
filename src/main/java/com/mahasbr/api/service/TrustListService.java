package com.mahasbr.api.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.repository.MstRegistryDetailsPageRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TrustListService {

	private final WebClient webClient;
	
	@Autowired
	MstRegistryDetailsPageRepository MstRegistryDetailsPageRepository;

	public TrustListService(WebClient.Builder builder) {
		// Increase buffer size (e.g., 10 MB)
		ExchangeStrategies strategies = ExchangeStrategies.builder()
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)).build();

		this.webClient = builder.baseUrl("https://uatoccsbr.mahait.org/api/sbr").exchangeStrategies(strategies).build();
	}
	
	
	public void fetchAndSaveTrustList() {
        String username = "SBR_Api";
        String password = "i6P7Q-R5qH_ssU";
        String credentials = username + ":" + password;
        String base64Creds = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        Flux<MstRegistryDetailsPageEntity> trustListFlux = webClient.get()
                .uri("/trustlist")
                .header("Authorization", "Basic " + base64Creds)
                .retrieve()
                .onStatus(status -> status.value() == 401,
                        response -> Mono.error(new RuntimeException("401 Unauthorized - Authentication Failed")))
                .bodyToFlux(MstRegistryDetailsPageEntity.class)
                .doOnNext(entity -> entity.setRegUserId(1L)); // set user ID

        // Save each entity to the database
        trustListFlux
                .flatMap(entity -> Mono.fromCallable(() -> MstRegistryDetailsPageRepository.save(entity))) // save asynchronously
                .subscribe(
                        saved -> System.out.println("Saved entity: "),
                        error -> System.err.println("Error saving entity: " + error.getMessage()),
                        () -> System.out.println("All entities saved successfully")
                );
    }

}
