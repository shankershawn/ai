package com.shankarsan.ai;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.task.VirtualThreadTaskExecutor;

@SpringBootApplication
@RequiredArgsConstructor
public class AiApplication {

  private static final Logger log = LoggerFactory.getLogger(AiApplication.class);

  private final ChatModel chatModel;

  private final VectorStore vectorStore;

  private final VirtualThreadTaskExecutor virtualThreadTaskExecutor;

  public static void main(String[] args) {
    SpringApplication.run(AiApplication.class, args);
  }
}
