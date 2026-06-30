package com.shankarsan.ai;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
@RequiredArgsConstructor
public class AiApplication {

  private static final Logger log = LoggerFactory.getLogger(AiApplication.class);

  private final OpenAiChatModel openAiChatModel;

  private final VectorStore vectorStore;

  public static void main(String[] args) {
    SpringApplication.run(AiApplication.class, args);
  }

  @Bean
  ApplicationRunner applicationRunner() {
    return args -> {
      List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
      CompletableFuture.runAsync(() -> {});

      for(int i = 0; i < 8; i++) {
        log.info("Calling OpenAI for the {} time", i + 1);
        completableFutures.add(CompletableFuture.runAsync(() -> {
          String response = openAiChatModel.call("What is survivorship bias?");
          log.info("Response from OpenAI: {}", response);
          vectorStore.add(List.of(new Document(response)));
        }));
      }
      completableFutures.forEach(CompletableFuture::join);

    };
  }
}
