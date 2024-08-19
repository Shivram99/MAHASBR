package com.mahasbr.util;

import org.springframework.stereotype.Component;

@Component
public class BRNGenerator {

	SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1);

	private final String STATECODE = "27";

	public String getBRNNumber() {

		String brnNumbere = "";
		String highOrder = "0000";
		String uniqueId = generator.nextId();
		brnNumbere = STATECODE + highOrder + uniqueId;

		return brnNumbere;
	}

}
