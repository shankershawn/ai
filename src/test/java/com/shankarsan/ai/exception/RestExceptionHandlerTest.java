package com.shankarsan.ai.exception;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;

class RestExceptionHandlerTest {

  private final RestExceptionHandler restExceptionHandler = new RestExceptionHandler();

  @Test
  void testHandleServiceException() {
    ServiceException ex = new ServiceException("Test Service Exception", new RuntimeException());
    ResponseEntity<ErrorResponse> response = restExceptionHandler.handleServiceException(ex);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().message()).isEqualTo("Test Service Exception");
  }

  @Test
  void testHandleMissingServletRequestParameterException() {
    MissingServletRequestParameterException ex =
        new MissingServletRequestParameterException("param", "String");
    ResponseEntity<ErrorResponse> response =
        restExceptionHandler.handleMissingServletRequestParameterException(ex);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().message())
        .contains(
            "Required request parameter 'param' for method parameter type String is not present");
  }

  @Test
  void testHandleConstraintViolationException() {
    ConstraintViolationException ex =
        new ConstraintViolationException("Validation error", Collections.emptySet());
    ResponseEntity<ErrorResponse> response =
        restExceptionHandler.handleConstraintViolationException(ex);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().message()).isEqualTo("Validation error");
  }
}
