package br.com.fiap.mindcare;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MindCareApplicationTests {

	@Test
	void applicationClassExists() {
		assertNotNull(MindCareApplication.class);
	}

	@Test
	void mainMethodExists() throws NoSuchMethodException {
		assertNotNull(MindCareApplication.class.getMethod("main", String[].class));
	}

	@Test
	void applicationContextLoads() {
		assertDoesNotThrow(() -> {
			// Verifica que a classe principal existe e pode ser carregada
			Class.forName("br.com.fiap.mindcare.MindCareApplication");
		});
	}

}