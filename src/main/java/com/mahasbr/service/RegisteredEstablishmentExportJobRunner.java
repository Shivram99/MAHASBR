package com.mahasbr.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class RegisteredEstablishmentExportJobRunner {

	private final RegisteredEstablishmentExportService registeredEstablishmentExportService;

	public RegisteredEstablishmentExportJobRunner(
			RegisteredEstablishmentExportService registeredEstablishmentExportService) {
		this.registeredEstablishmentExportService = registeredEstablishmentExportService;
	}

	@Async("mvcTaskExecutor")
	public CompletableFuture<Void> runAsync(String jobId) {
		registeredEstablishmentExportService.runExportJob(jobId);
		return CompletableFuture.completedFuture(null);
	}
}
