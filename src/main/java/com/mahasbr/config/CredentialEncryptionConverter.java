package com.mahasbr.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Encrypts external-API passwords before they are written to the database (AES-256-GCM).
 *
 * <p>The key comes from the environment variable {@code REGISTRY_API_CREDENTIAL_KEY} (or the
 * JVM property {@code registry.api.credential-key}); any passphrase works, it is hashed to a
 * 256-bit key. Set it once per server and never change it, or stored passwords can no longer
 * be read and must be re-entered.
 *
 * <p>Without a key, values are stored as plain text and a warning is logged, so existing
 * setups keep working. Values written before encryption was enabled (no {@code ENC:} prefix)
 * are still read, and get encrypted the next time they are saved.
 */
@Converter
public class CredentialEncryptionConverter implements AttributeConverter<String, String> {

	private static final Logger log = LoggerFactory.getLogger(CredentialEncryptionConverter.class);

	static final String PREFIX = "ENC:";
	private static final String ENV_KEY = "REGISTRY_API_CREDENTIAL_KEY";
	private static final String PROPERTY_KEY = "registry.api.credential-key";
	private static final int IV_LENGTH = 12;
	private static final int TAG_BITS = 128;
	private static final SecureRandom RANDOM = new SecureRandom();

	private static volatile boolean warnedAboutMissingKey;

	@Override
	public String convertToDatabaseColumn(String plain) {
		if (plain == null || plain.isEmpty()) {
			return plain;
		}
		SecretKeySpec key = key();
		if (key == null) {
			if (!warnedAboutMissingKey) {
				warnedAboutMissingKey = true;
				log.warn("{} is not set: registry API passwords are stored WITHOUT encryption.", ENV_KEY);
			}
			return plain;
		}
		try {
			byte[] iv = new byte[IV_LENGTH];
			RANDOM.nextBytes(iv);
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
			byte[] encrypted = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));

			byte[] combined = new byte[iv.length + encrypted.length];
			System.arraycopy(iv, 0, combined, 0, iv.length);
			System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
			return PREFIX + Base64.getEncoder().encodeToString(combined);
		} catch (Exception e) {
			throw new IllegalStateException("Could not encrypt registry API credential", e);
		}
	}

	@Override
	public String convertToEntityAttribute(String stored) {
		if (stored == null || !stored.startsWith(PREFIX)) {
			return stored;   // empty, or saved before encryption was enabled
		}
		SecretKeySpec key = key();
		if (key == null) {
			// Fail loudly: returning null here would make Hibernate overwrite the stored value.
			throw new IllegalStateException(
					"A registry API password is encrypted but " + ENV_KEY + " is not set on this server.");
		}
		try {
			byte[] combined = Base64.getDecoder().decode(stored.substring(PREFIX.length()));
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, combined, 0, IV_LENGTH));
			byte[] plain = cipher.doFinal(combined, IV_LENGTH, combined.length - IV_LENGTH);
			return new String(plain, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Could not decrypt a registry API password. Was " + ENV_KEY + " changed?", e);
		}
	}

	private static SecretKeySpec key() {
		String passphrase = System.getenv(ENV_KEY);
		if (passphrase == null || passphrase.isBlank()) {
			passphrase = System.getProperty(PROPERTY_KEY);
		}
		if (passphrase == null || passphrase.isBlank()) {
			return null;
		}
		try {
			byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(passphrase.getBytes(StandardCharsets.UTF_8));
			return new SecretKeySpec(keyBytes, "AES");
		} catch (Exception e) {
			throw new IllegalStateException("Could not derive registry API credential key", e);
		}
	}
}
