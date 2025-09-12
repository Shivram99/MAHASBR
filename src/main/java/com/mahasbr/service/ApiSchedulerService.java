package com.mahasbr.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ApiSchedulerService {

    private final RegistryApiService apiClientService;
    
//	@Autowired
//    private TrustListService trustListService;

    public ApiSchedulerService(RegistryApiService apiClientService) {
        this.apiClientService = apiClientService;
    }

//    @Scheduled(cron = "0 0 23 * * *") // every day at 11 PM
    @Scheduled(fixedRate = 10000)
    public void fetchGovApis() {
        System.out.println("🚀 Scheduler started at 11 PM...");
        
//        trustListService.fetchAndSaveTrustList();

        CompletableFuture.allOf(
                apiClientService.fetchCharity()
////                apiClientService.fetchDish(),
////                apiClientService.fetchLabour(),
////                apiClientService.fetchCoop(),
////                apiClientService.fetchKvib(),
////                apiClientService.fetchMcgm(),
////                apiClientService.fetchMsme(),
////                apiClientService.fetchMca(),
////                apiClientService.fetchGstn()
        ).join();

        System.out.println("🎯 Scheduler finished.");
    }
}