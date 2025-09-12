package com.mahasbr.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.repository.MstRegistryDetailsPageRepository;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class RegistryApiService {

	private final WebClient webClient;
	private final MultiAuthService authService;
	private final MstRegistryDetailsPageRepository repository;
	private final ApiLogService apiLogService;
	private final ObjectMapper mapper = new ObjectMapper();

	private static final String CHARITY_URL = "https://sbrlmsapi.mahaitgov.in/api/SBR/GetSBRShopData";
//    private static final String DISH_URL = "https://sbrlmsapi.mahaitgov.in/api/SBR/GetDishData";
//    private static final String LABOUR_URL = "https://sbrlmsapi.mahaitgov.in/api/SBR/GetLabourData";
//    private static final String COOP_URL = "https://sbrlmsapi.mahaitgov.in/api/SBR/GetCoOperativeData";
//    private static final String KVIB_URL = "https://sbrlmsapi.mahaitgov.in/api/SBR/GetKVIBData";
//    private static final String MCGM_URL = "https://sbrlmsapi.mahaitgov.in/api/SBR/GetMCGMData";
//    private static final String MSME_URL = "https://sbrlmsapi.mahaitgov.in/api/SBR/GetMSMEData";
//    private static final String MCA_URL = "https://sbrlmsapi.mahaitgov.in/api/SBR/GetMCAData";
//    private static final String GSTN_URL = "https://sbrlmsapi.mahaitgov.in/api/SBR/GetGSTNData";

	public RegistryApiService(WebClient webClient, MultiAuthService authService,
			MstRegistryDetailsPageRepository repository, ApiLogService apiLogService) {
		this.webClient = webClient;
		this.authService = authService;
		this.repository = repository;
		this.apiLogService = apiLogService;
	}

	@Async
	@Retryable(value = { RuntimeException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000))
	public CompletableFuture<Void> fetchAndSave(ApiAuthConfig config, String apiName, String url) {
		try {
			String token = authService.getToken(config);

			String response = webClient.post().uri(url).header("Authorization", "Bearer " + token).retrieve()
					.bodyToMono(String.class).block();

			if (response != null && !response.isEmpty()) {
                List<MstRegistryDetailsPageEntity> entities = mapper.readValue(
                        response,
                        new TypeReference<List<MstRegistryDetailsPageEntity>>() {}
                );
				try {
					Object json = mapper.readValue(response, Object.class);
					String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
					System.out.println("Pretty JSON Response:\n" + prettyJson);
				} catch (Exception e) {
					System.out.println("⚠️ Failed to pretty-print JSON: " + e.getMessage());
				}
                for (MstRegistryDetailsPageEntity entity : entities) {
                    System.out.println(entity);
                }
                //repository.saveAll(entities);
                apiLogService.log(apiName, url, "SUCCESS", "Saved " + entities.size() + " records");
			} else {
				apiLogService.log(apiName, url, "FAILURE", "No data received");
			}
		} catch (Exception e) {
			apiLogService.log(apiName, url, "FAILURE", e.getMessage());
			throw new RuntimeException(e);
		}

		return CompletableFuture.completedFuture(null);
	}

	// Convenience methods
	public CompletableFuture<Void> fetchCharity() {
		return fetchAndSave(ApiAuthConfig.CHARITY, "Charity Commissioner", CHARITY_URL);
	}
//    public CompletableFuture<Void> fetchDish() { return fetchAndSave(ApiAuthConfig.DISH, "DISH", DISH_URL); }
//    public CompletableFuture<Void> fetchLabour() { return fetchAndSave(ApiAuthConfig.LABOUR, "Labour", LABOUR_URL); }
//    public CompletableFuture<Void> fetchCoop() { return fetchAndSave(ApiAuthConfig.COOP, "Co-Op Societies", COOP_URL); }
//    public CompletableFuture<Void> fetchKvib() { return fetchAndSave(ApiAuthConfig.KVIB, "KVIB", KVIB_URL); }
//    public CompletableFuture<Void> fetchMcgm() { return fetchAndSave(ApiAuthConfig.MCGM, "MCGM", MCGM_URL); }
//    public CompletableFuture<Void> fetchMsme() { return fetchAndSave(ApiAuthConfig.MSME, "MSME", MSME_URL); }
//    public CompletableFuture<Void> fetchMca() { return fetchAndSave(ApiAuthConfig.MCA, "MCA", MCA_URL); }
//    public CompletableFuture<Void> fetchGstn() { return fetchAndSave(ApiAuthConfig.GSTN, "GSTN", GSTN_URL); }

	public CompletableFuture<Void> fetchAllApis() {
		return CompletableFuture.allOf(fetchCharity()
//                fetchDish(),
//                fetchLabour(),
//                fetchCoop(),
//                fetchKvib(),
//                fetchMcgm(),
//                fetchMsme(),
//                fetchMca(),
//                fetchGstn()
		);
	}
}
