package com.shankarsan.ai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.VirtualThreadTaskExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
      "spring.datasource.username=sa",
      "spring.datasource.password=",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.autoconfigure.exclude="
          + "org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration,"
          + "org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration"
    })
@Import(AiApplicationTestConfiguration.class)
class AiApplicationTests {

  @Autowired
  private VirtualThreadTaskExecutor virtualThreadTaskExecutor;

  @Test
  void contextLoads() {
    assertNotNull("hello");
  }

  @Test
  void virtualThreadTaskExecutorIsConfigured() {
    assertThat(virtualThreadTaskExecutor).isNotNull();
  }
}
