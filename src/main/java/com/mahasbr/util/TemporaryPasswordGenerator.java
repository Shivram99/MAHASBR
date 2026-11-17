package com.mahasbr.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates one-time passwords for new accounts. Every password contains at least one
 * uppercase letter, lowercase letter, digit and symbol, so it satisfies the same rule the
 * change-password screen enforces. Look-alike characters (0/O, 1/l/I) are left out because
 * admins read these passwords out to users.
 */
public final class TemporaryPasswordGenerator {

	private static final String UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ";
	private static final String LOWER = "abcdefghijkmnpqrstuvwxyz";
	private static final String DIGITS = "23456789";
	private static final String SYMBOLS = "@#$%&*!?";
	private static final String ALL = UPPER + LOWER + DIGITS + SYMBOLS;
	private static final int LENGTH = 12;

	private static final SecureRandom RANDOM = new SecureRandom();

	private TemporaryPasswordGenerator() {
	}

	public static String generate() {
		List<Character> chars = new ArrayList<>(LENGTH);
		chars.add(pick(UPPER));
		chars.add(pick(LOWER));
		chars.add(pick(DIGITS));
		chars.add(pick(SYMBOLS));
		while (chars.size() < LENGTH) {
			chars.add(pick(ALL));
		}
		Collections.shuffle(chars, RANDOM);

		StringBuilder password = new StringBuilder(LENGTH);
		chars.forEach(password::append);
		return password.toString();
	}

	private static char pick(String source) {
		return source.charAt(RANDOM.nextInt(source.length()));
	}
}
