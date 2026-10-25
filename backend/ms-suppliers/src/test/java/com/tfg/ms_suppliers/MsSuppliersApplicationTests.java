package com.tfg.ms_suppliers;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class MsSuppliersApplicationTests {

	// RabbitMQ esta desactivado en los tests, asi que se simula el RabbitTemplate
	@MockBean
	private RabbitTemplate rabbitTemplate;

	@Test
	void contextLoads() {
	}

}
