package com.shankarsan.ai.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ServiceExceptionTest {

  @Test
  void testServiceException() {
    RuntimeException cause = new RuntimeException("Root cause");
    ServiceException serviceException = new ServiceException("Service error", cause);
    assertThat(serviceException.getMessage()).isEqualTo("Service error");
    assertThat(serviceException.getCause()).isEqualTo(cause);
  }
}
