package com.shankarsan.ai.feature;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = {
      "spring.autoconfigure.exclude="
          + "org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration,"
          + "org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration"
    })
@AutoConfigureMockMvc
@ActiveProfiles("feature-test")
@Import(FeatureTestConfiguration.class)
public class CucumberSpringConfiguration {
}
