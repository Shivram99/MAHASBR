package com.mahasbr.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.mahasbr.entity.MstRegistryDetailsPageEntity;

class RegistryRecordSanitizerTest {

	@Test
	void blankTextBecomesNullAndTextIsTrimmed() {
		MstRegistryDetailsPageEntity e = new MstRegistryDetailsPageEntity();
		e.setHouseNo("   ");
		e.setStreetName("  MG Road ");

		RegistryRecordSanitizer.sanitize(e);

		assertNull(e.getHouseNo());
		assertEquals("MG Road", e.getStreetName());
	}

	@Test
	void tooLongTextIsCutToTheColumnSizeInsteadOfFailing() {
		MstRegistryDetailsPageEntity e = new MstRegistryDetailsPageEntity();
		e.setPanNumber("ABCDE1234FXYZ");   // column holds 10
		e.setDistrict("x".repeat(150));      // column holds 100

		RegistryRecordSanitizer.sanitize(e);

		assertEquals("ABCDE1234F", e.getPanNumber());
		assertEquals(100, e.getDistrict().length());
	}

	@Test
	void invalidEmailIsLeftBlank() {
		MstRegistryDetailsPageEntity e = new MstRegistryDetailsPageEntity();
		e.setEmailAddress("not an email");
		e.setHeadOfficeEmailAddress("ok@example.com");

		RegistryRecordSanitizer.sanitize(e);

		assertNull(e.getEmailAddress());
		assertEquals("ok@example.com", e.getHeadOfficeEmailAddress());
	}
}
