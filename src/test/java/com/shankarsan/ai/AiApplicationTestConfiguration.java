package com.shankarsan.ai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@TestConfiguration
public class AiApplicationTestConfiguration {

  @Bean
  @Primary
  ChatModel testChatModel() {
    return new ChatModel() {
      @Override
      public ChatResponse call(Prompt prompt) {
        return new ChatResponse(List.of(new Generation("test-response")));
      }

      @Override
      public ChatOptions getDefaultOptions() {
        return OpenAiChatOptions.builder().build();
      }
    };
  }

  @Bean
  @Primary
  VectorStore testVectorStore() {
    List<Document> documents = new CopyOnWriteArrayList<>();
    return new VectorStore() {
      @Override
      public void add(List<Document> docs) {
        documents.addAll(docs);
      }

      @Override
      public Optional<Boolean> delete(List<String> idList) {
        documents.removeIf(document -> idList.contains(document.getId()));
        return Optional.of(true);
      }

      @Override
      public List<Document> similaritySearch(SearchRequest request) {
        return List.copyOf(documents);
      }
    };
  }
}
