package com.mahasbr.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.mahasbr.model.ChangePasswordModel;

class TemporaryPasswordGeneratorTest {

	@Test
	void generatedPasswordsMeetTheStrengthRule() {
		for (int i = 0; i < 500; i++) {
			String password = TemporaryPasswordGenerator.generate();
			assertEquals(12, password.length());
			assertTrue(password.matches(ChangePasswordModel.PASSWORD_REGEX),
					"Generated password does not satisfy the rule: " + password);
		}
	}

	@Test
	void generatedPasswordsAreNotRepeated() {
		Set<String> seen = new HashSet<>();
		for (int i = 0; i < 500; i++) {
			assertTrue(seen.add(TemporaryPasswordGenerator.generate()));
		}
	}
}
