package com.k3.examen;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamenApplicationTests {

	@Test
	void contextLoads() {
		// Simple test to verify basic setup
		ExamenApplication app = new ExamenApplication();
		assertThat(app).isNotNull();
	}

}
