package com.shankarsan.ai.feature.steps;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class ConfigSteps {

  @Autowired
  private Environment environment;

  @Autowired
  private DataSource dataSource;

  private StandardEnvironment resolvedEnvironment;
  private String lastEnvVar;
  private String lastEnvValue;

  @Given("the application configuration is loaded")
  public void applicationConfigurationIsLoaded() {
    assertThat(environment).isNotNull();
  }

  @Given("the Spring datasource URL is configured for H2")
  public void datasourceUrlConfiguredForH2() {
    assertThat(environment.getProperty("spring.datasource.url"))
        .startsWith("jdbc:h2:");
  }

  @Given("datasource credentials are provided via environment variables or defaults")
  public void datasourceCredentialsProvided() {
    assertThat(environment.containsProperty("spring.datasource.username")).isTrue();
    assertThat(environment.containsProperty("spring.datasource.password")).isTrue();
  }

  @Given("environment variable {string} is set to {string}")
  public void environmentVariableIsSet(String envVar, String value) {
    lastEnvVar = envVar;
    lastEnvValue = value;
    Map<String, Object> envMap = new HashMap<>();
    envMap.put(envVar, value);
    resolvedEnvironment = new StandardEnvironment();
    resolvedEnvironment
        .getPropertySources()
        .addFirst(new SystemEnvironmentPropertySource("featureTestEnv", envMap));
  }

  @When("the application configuration is resolved")
  public void applicationConfigurationIsResolved() {
    assertThat(resolvedEnvironment).isNotNull();
    assertThat(lastEnvVar).isNotBlank();
    assertThat(lastEnvValue).isNotBlank();
  }

  @Then("the pgvector index type should be {string}")
  public void pgvectorIndexType(String indexType) {
    assertThat(environment.getProperty("spring.ai.vectorstore.pgvector.index-type"))
        .isEqualTo(indexType);
  }

  @Then("the pgvector distance type should be {string}")
  public void pgvectorDistanceType(String distanceType) {
    assertThat(environment.getProperty("spring.ai.vectorstore.pgvector.distance-type"))
        .isEqualTo(distanceType);
  }

  @Then("the embedding dimension should be {int}")
  public void embeddingDimension(int dimension) {
    assertThat(environment.getProperty("spring.ai.vectorstore.pgvector.embedding-dimension", Integer.class))
        .isEqualTo(dimension);
  }

  @Then("schema initialization should be enabled")
  public void schemaInitializationEnabled() {
    assertThat(environment.getProperty("spring.ai.vectorstore.pgvector.initialize-schema", Boolean.class))
        .isTrue();
  }

  @Then("the content size limit should be {int}")
  public void contentSizeLimit(int contentSize) {
    assertThat(environment.getProperty("spring.ai.vectorstore.pgvector.content-size", Integer.class))
        .isEqualTo(contentSize);
  }

  @Then("a connection to the configured H2 database should be established")
  public void h2ConnectionEstablished() throws Exception {
    assertThat(environment.getProperty("spring.datasource.url")).startsWith("jdbc:h2:");
    try (Connection connection = dataSource.getConnection()) {
      assertThat(connection.isValid(1)).isTrue();
    }
  }

  @Then("the datasource property {string} should equal {string}")
  public void datasourcePropertyShouldEqual(String property, String value) {
    assertThat(resolvedEnvironment.getProperty(property)).isEqualTo(value);
  }

  @Then("the OpenAI chat model should be {string}")
  public void openAiChatModelShouldBe(String model) {
    assertThat(environment.getProperty("spring.ai.openai.chat.options.model")).isEqualTo(model);
  }

  @Then("the OpenAI embedding model should be {string}")
  public void openAiEmbeddingModelShouldBe(String model) {
    assertThat(environment.getProperty("spring.ai.openai.embedding.options.model")).isEqualTo(model);
  }

  @Then("the retry max attempts should be {int}")
  public void retryMaxAttemptsShouldBe(int maxAttempts) {
    assertThat(environment.getProperty("spring.ai.retry.max-attempts", Integer.class))
        .isEqualTo(maxAttempts);
  }

  @Then("client error retries should be disabled")
  public void clientErrorRetriesDisabled() {
    assertThat(environment.getProperty("spring.ai.retry.on-client-errors", Boolean.class)).isFalse();
  }

  @Then("the retry backoff initial interval should be {string}")
  public void retryBackoffInitialInterval(String interval) {
    assertThat(environment.getProperty("spring.ai.retry.backoff.initial-interval")).isEqualTo(interval);
  }

  @Then("the retry backoff multiplier should be {int}")
  public void retryBackoffMultiplier(int multiplier) {
    assertThat(environment.getProperty("spring.ai.retry.backoff.multiplier", Integer.class))
        .isEqualTo(multiplier);
  }

  @Then("the retry backoff max interval should be {string}")
  public void retryBackoffMaxInterval(String interval) {
    assertThat(environment.getProperty("spring.ai.retry.backoff.max-interval")).isEqualTo(interval);
  }
}
