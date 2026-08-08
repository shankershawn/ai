package com.shankarsan.ai.exception;

public class ServiceException extends RuntimeException {

  public ServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
