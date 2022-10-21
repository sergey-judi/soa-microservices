package com.api.gateway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiGatewayApplicationTest {

  @Test
  void contextLoads() {
    Assertions.assertDoesNotThrow(
        () -> ApiGatewayApplication.main(new String[] {})
    );
  }

}