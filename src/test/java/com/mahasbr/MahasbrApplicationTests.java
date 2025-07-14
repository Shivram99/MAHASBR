package com.mahasbr;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = SbrBackEndProjectApplication.class)

//@TestPropertySource(locations = "classpath:application-uat.properties")
//@ActiveProfiles("uat")
class MahasbrApplicationTests {

	@Test
	void contextLoads() {
	}

}
