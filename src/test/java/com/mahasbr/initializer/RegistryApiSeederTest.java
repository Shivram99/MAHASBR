package com.mahasbr.initializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mahasbr.entity.RegistryApiAuthConfigEntity;
import com.mahasbr.entity.RegistryMasterEntity;
import com.mahasbr.repository.RegistryApiAuthConfigRepository;
import com.mahasbr.repository.RegistryMasterRepository;

@ExtendWith(MockitoExtension.class)
class RegistryApiSeederTest {

	@Mock
	private RegistryApiAuthConfigRepository configRepository;
	@Mock
	private RegistryMasterRepository registryRepository;

	private RegistryApiSeeder seeder;

	@BeforeEach
	void setUp() {
		seeder = new RegistryApiSeeder(configRepository, registryRepository);
		lenient().when(configRepository.findByServiceNameCodeIgnoreCase(any())).thenReturn(Optional.empty());
		lenient().when(registryRepository.findById(any())).thenReturn(Optional.empty());
	}

	@Test
	void addsAllNineSwitchedOffAndTakesLoginFromEnvironment() {
		RegistryMasterEntity labour = new RegistryMasterEntity();
		labour.setId(23L);
		when(registryRepository.findById(23L)).thenReturn(Optional.of(labour));
		Map<String, String> env = Map.of("REGISTRY_API_LABOUR_USERNAME", "u1", "REGISTRY_API_LABOUR_PASSWORD", "p1");

		assertEquals(9, seeder.seed(env::get));

		ArgumentCaptor<RegistryApiAuthConfigEntity> saved = ArgumentCaptor.forClass(RegistryApiAuthConfigEntity.class);
		verify(configRepository, times(9)).save(saved.capture());
		saved.getAllValues().forEach(e -> assertFalse(e.getActive()));
		RegistryApiAuthConfigEntity labourRow = saved.getAllValues().stream()
				.filter(e -> e.getServiceNameCode().equals("LABOUR")).findFirst().orElseThrow();
		assertEquals("u1", labourRow.getUsername());
		assertEquals("p1", labourRow.getPassword());
		assertEquals(23L, labourRow.getRegistry().getId());
	}

	@Test
	void coOpUsesAnEnvironmentSafeName() {
		Map<String, String> env = Map.of("REGISTRY_API_CO_OP_USERNAME", "coop");
		seeder.seed(env::get);

		ArgumentCaptor<RegistryApiAuthConfigEntity> saved = ArgumentCaptor.forClass(RegistryApiAuthConfigEntity.class);
		verify(configRepository, times(9)).save(saved.capture());
		assertEquals("coop", saved.getAllValues().stream().filter(e -> e.getServiceNameCode().equals("CO-OP"))
				.findFirst().orElseThrow().getUsername());
	}

	@Test
	void existingEntriesAreLeftAlone() {
		when(configRepository.findByServiceNameCodeIgnoreCase(any()))
				.thenReturn(Optional.of(new RegistryApiAuthConfigEntity()));

		assertEquals(0, seeder.seed(k -> null));
		verify(configRepository, never()).save(any());
	}
}
