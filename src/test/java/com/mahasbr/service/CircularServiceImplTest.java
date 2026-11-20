package com.mahasbr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mahasbr.config.FileStorageProperties;
import com.mahasbr.dto.CircularResponseDTO;
import com.mahasbr.entity.Circular;
import com.mahasbr.repository.CircularRepository;

@ExtendWith(MockitoExtension.class)
class CircularServiceImplTest {

	@Mock
	private CircularRepository repository;

	private CircularServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new CircularServiceImpl(repository, mock(FileStorageService.class), mock(FileStorageProperties.class));
	}

	private static Circular circular(long id, String date, Boolean active) {
		Circular c = new Circular();
		c.setId(id);
		c.setSubject("Circular " + id);
		c.setCircularDate(LocalDate.parse(date));
		c.setFilePath("circulars/general/" + id + ".pdf");
		c.setActive(active);
		return c;
	}

	@Test
	void publicListHasOnlyActiveCircularsNewestFirst() {
		when(repository.findAll()).thenReturn(List.of(
				circular(1, "2026-01-01", true),
				circular(2, "2026-05-01", false),     // hidden
				circular(3, "2026-03-01", null),      // saved before the flag existed: counts as active
				circular(4, "2026-09-01", true)));

		List<CircularResponseDTO> shown = service.getActiveCirculars();

		assertEquals(List.of(4L, 3L, 1L), shown.stream().map(CircularResponseDTO::getId).toList());
	}

	@Test
	void adminListStillHasEverything() {
		when(repository.findAll()).thenReturn(List.of(circular(1, "2026-01-01", true), circular(2, "2026-05-01", false)));

		List<CircularResponseDTO> all = service.getAllCirculars();

		assertEquals(2, all.size());
		assertTrue(all.get(0).getActive());
		assertFalse(all.get(1).getActive());
	}

	@Test
	void switchingOffHidesItFromThePublicList() {
		Circular c = circular(1, "2026-01-01", true);
		when(repository.findById(1L)).thenReturn(Optional.of(c));
		when(repository.save(any(Circular.class))).thenAnswer(inv -> inv.getArgument(0));

		CircularResponseDTO result = service.setActive(1L, false);

		assertFalse(result.getActive());
		assertFalse(c.getActive());
	}
}
