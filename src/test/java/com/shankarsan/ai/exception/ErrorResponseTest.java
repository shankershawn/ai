package com.shankarsan.ai.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ErrorResponseTest {

  @Test
  void testErrorResponse() {
    ErrorResponse errorResponse =
        new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Invalid input");
    assertThat(errorResponse.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(errorResponse.error()).isEqualTo("Bad Request");
    assertThat(errorResponse.message()).isEqualTo("Invalid input");
  }
}
