package com.shankarsan.ai.aop;

import org.springframework.stereotype.Service;

@Service
public class TestService {

  public String doSomething(String input) {
    return "Result: " + input;
  }

  public void doSomethingThatThrows() {
    throw new RuntimeException("Test Exception");
  }
}
