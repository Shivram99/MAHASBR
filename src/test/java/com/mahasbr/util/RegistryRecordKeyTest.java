package com.mahasbr.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class RegistryRecordKeyTest {

	private static String key(String regNo, String name) {
		return RegistryRecordKey.compute(regNo, name, "ABCDE1234F", null, "Pune", "Haveli", "Wagholi",
				LocalDate.of(2020, 1, 5));
	}

	@Test
	void sameRecordGivesSameKeyDespiteCaseAndSpaces() {
		assertEquals(key(null, "Sai  Traders"), key(null, "  SAI TRADERS "));
	}

	@Test
	void registrationNumberIsTheIdentityWhenPresent() {
		assertEquals(key("MH/123", "Old Name"), key(" mh/123 ", "New Name"));
		assertNotEquals(key("MH/123", "X"), key("MH/124", "X"));
	}

	@Test
	void differentEstablishmentsGetDifferentKeys() {
		assertNotEquals(key(null, "Sai Traders"), key(null, "Om Traders"));
	}

	@Test
	void keyFitsTheColumn() {
		assertEquals(64, key(null, "x").length());
	}
}
