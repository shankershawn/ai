package com.shankarsan.ai.exception;

public record ErrorResponse(int status, String error, String message) {}
