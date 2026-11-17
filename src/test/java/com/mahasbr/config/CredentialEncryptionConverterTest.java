package com.mahasbr.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class CredentialEncryptionConverterTest {

	private final CredentialEncryptionConverter converter = new CredentialEncryptionConverter();

	@AfterEach
	void clearKey() {
		System.clearProperty("registry.api.credential-key");
	}

	@Test
	void encryptsAndDecryptsWhenKeyIsSet() {
		System.setProperty("registry.api.credential-key", "test-passphrase");

		String stored = converter.convertToDatabaseColumn("s3cret!");

		assertTrue(stored.startsWith("ENC:"));
		assertNotEquals("s3cret!", stored);
		assertEquals("s3cret!", converter.convertToEntityAttribute(stored));
	}

	@Test
	void sameValueEncryptsDifferentlyEachTime() {
		System.setProperty("registry.api.credential-key", "test-passphrase");
		assertNotEquals(converter.convertToDatabaseColumn("abc"), converter.convertToDatabaseColumn("abc"));
	}

	@Test
	void plainValuesStillReadAndWithoutKeyAreStoredAsIs() {
		assertEquals("legacy", converter.convertToEntityAttribute("legacy"));
		assertEquals("plain", converter.convertToDatabaseColumn("plain"));
	}

	@Test
	void encryptedValueWithoutKeyFailsInsteadOfReturningNull() {
		System.setProperty("registry.api.credential-key", "test-passphrase");
		String stored = converter.convertToDatabaseColumn("abc");
		System.clearProperty("registry.api.credential-key");

		assertThrows(IllegalStateException.class, () -> converter.convertToEntityAttribute(stored));
	}
}
