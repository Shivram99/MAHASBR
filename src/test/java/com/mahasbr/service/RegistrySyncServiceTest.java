package com.mahasbr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.entity.RegistryApiAuthConfigEntity;
import com.mahasbr.entity.RegistryMasterEntity;
import com.mahasbr.repository.RegistryApiAuthConfigRepository;

@ExtendWith(MockitoExtension.class)
class RegistrySyncServiceTest {

	@Mock
	private RegistryApiAuthConfigRepository configRepository;
	@Mock
	private RegistryApiClient apiClient;
	@Mock
	private RegistryRecordUpsertService upsertService;
	@Mock
	private ApiLogService apiLogService;

	private RegistrySyncService service;

	@BeforeEach
	void setUp() {
		service = new RegistrySyncService(configRepository, apiClient, upsertService, apiLogService,
				new ObjectMapper(), "Asia/Kolkata", 7);
	}

	private static RegistryApiAuthConfigEntity config(boolean active, LocalDate from, LocalDate lastSynced) {
		RegistryApiAuthConfigEntity config = new RegistryApiAuthConfigEntity();
		config.setId(1L);
		config.setActive(active);
		config.setSyncFromDate(from);
		config.setLastSyncedTo(lastSynced);
		RegistryMasterEntity registry = new RegistryMasterEntity();
		registry.setId(23L);
		config.setRegistry(registry);
		return config;
	}

	@Test
	void firstRunStartsAtSyncFromDate() {
		assertEquals(LocalDate.of(2015, 1, 1), service.startDate(config(true, LocalDate.of(2015, 1, 1), null)));
	}

	@Test
	void laterRunResumesAfterLastSyncMinusOverlap() {
		// last synced 10 Mar -> resume 11 Mar minus 7 days overlap = 4 Mar
		assertEquals(LocalDate.of(2026, 3, 4),
				service.startDate(config(true, LocalDate.of(2015, 1, 1), LocalDate.of(2026, 3, 10))));
	}

	@Test
	void resumeNeverGoesBeforeSyncFromDate() {
		assertEquals(LocalDate.of(2026, 3, 1),
				service.startDate(config(true, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 3))));
	}

	@Test
	void inactiveApiIsNeverCalled() {
		when(configRepository.findById(1L)).thenReturn(Optional.of(config(false, LocalDate.of(2015, 1, 1), null)));

		service.runSync(1L, "nightly");

		verify(apiClient, never()).login(any());
		verify(upsertService, never()).upsert(any(), any());
	}

	@Test
	void runNowRefusesAnInactiveApi() {
		when(configRepository.findById(1L)).thenReturn(Optional.of(config(false, LocalDate.of(2015, 1, 1), null)));

		assertThrows(IllegalArgumentException.class, () -> service.startSync(1L));
		verify(apiClient, never()).login(any());
	}

	@Test
	void failedLoginIsReportedAsFailedNotThrown() {
		RegistryApiAuthConfigEntity config = config(true, LocalDate.of(2015, 1, 1), null);
		config.setServiceName("Labour");
		when(configRepository.findById(1L)).thenReturn(Optional.of(config));
		when(upsertService.backfillBatch(23)).thenReturn(0);
		when(apiClient.login(config)).thenThrow(new IllegalStateException("bad login"));

		service.runSync(1L, "manual");

		verify(configRepository).updateRunStatus(eq(1L), eq("FAILED"), any(), any());
	}
}
