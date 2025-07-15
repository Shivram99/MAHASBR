package com.mahasbr.util;

import org.springframework.stereotype.Component;

@Component
public class StringUtils {
	public String safeUpperCase(String str) {
	    return str != null ? str.trim().toUpperCase() : null;
	}
}
