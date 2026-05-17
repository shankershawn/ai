package com.shankarsan.ai;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class AiApplication {

  private static final Logger log = LoggerFactory.getLogger(AiApplication.class);

  private final OpenAiChatModel openAiChatModel;

  public static void main(String[] args) {
    SpringApplication.run(AiApplication.class, args);
  }

  @Bean
  CommandLineRunner commandLineRunner() {
    return args -> {
      String response = openAiChatModel.call("What is survivorship bias?");
      log.info("Response from OpenAI: {}", response);
    };
  }
}
