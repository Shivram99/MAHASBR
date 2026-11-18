package com.mahasbr.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.mahasbr.entity.MstRegistryDetailsPageEntity;

class RegistrationStatusStorageTest {

	@Test
	void registrationStatusIsStoredAsSentNotCutToOneCharacter() {
		MstRegistryDetailsPageEntity entity = new MstRegistryDetailsPageEntity();
		entity.setRegistrationStatus(" ISSUED ");

		RegistryRecordSanitizer.sanitize(entity);

		assertEquals("ISSUED", entity.getRegistrationStatus());
	}
}
