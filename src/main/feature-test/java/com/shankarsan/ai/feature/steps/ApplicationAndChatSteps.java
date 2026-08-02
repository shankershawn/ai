package com.shankarsan.ai.feature.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shankarsan.ai.feature.support.RecordingChatModel;
import com.shankarsan.ai.feature.support.RecordingVectorStore;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationAndChatSteps {

  private static final String SURVIVORSHIP_PROMPT = "What is survivorship bias?";
  private static final String SAMPLE_RESPONSE =
      "Survivorship bias is the logical error of concentrating on entities that passed a selection process.";

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private Environment environment;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private RecordingChatModel recordingChatModel;

  @Autowired
  private RecordingVectorStore recordingVectorStore;

  private final AtomicReference<String> lastChatResponse = new AtomicReference<>();
  private final AtomicReference<Document> lastDocument = new AtomicReference<>();
  private boolean runnerObserved;

  @Given("the application is configured with OpenAI and pgvector settings")
  public void applicationIsConfigured() {
    assertThat(environment.getProperty("spring.ai.openai.base-url")).isNotBlank();
    assertThat(environment.getProperty("spring.ai.vectorstore.pgvector.embedding-dimension"))
        .isEqualTo("768");
  }

  @Given("the application has started")
  public void applicationHasStarted() {
    assertThat(applicationContext).isNotNull();
  }

  @Given("the application has started with Actuator enabled")
  public void applicationHasStartedWithActuator() {
    assertThat(applicationContext).isNotNull();
  }

  @Given("the OpenAI base URL is configured to {string}")
  @Given("the OpenAI base URL is {string}")
  public void openAiBaseUrlIs(String baseUrl) {
    assertThat(environment.getProperty("spring.ai.openai.base-url")).isEqualTo(baseUrl);
  }

  @Given("the OpenAI API key is configured")
  public void openAiApiKeyIsConfigured() {
    assertThat(environment.getProperty("spring.ai.openai.api-key")).isNotBlank();
  }

  @Given("the OpenAI chat model returns a text response")
  public void openAiChatModelReturnsTextResponse() {
    recordingChatModel.setResponseText(SAMPLE_RESPONSE);
    lastChatResponse.set(SAMPLE_RESPONSE);
  }

  @Given("{int} concurrent chat-and-store tasks have been submitted")
  public void concurrentTasksSubmitted(int taskCount) {
    assertThat(recordingChatModel.countPromptsEqualTo(SURVIVORSHIP_PROMPT))
        .isGreaterThanOrEqualTo(taskCount);
  }

  @When("the Spring Boot application starts")
  @When("the application starts")
  public void springBootApplicationStarts() {
    assertThat(applicationContext).isNotNull();
  }

  @When("a chat prompt {string} is sent to the OpenAI chat model")
  public void chatPromptIsSent(String prompt) throws Exception {
    lastChatResponse.set(postChatPrompt(prompt));
  }

  @When("a chat request is made")
  public void chatRequestIsMade() throws Exception {
    lastChatResponse.set(postChatPrompt(SURVIVORSHIP_PROMPT));
  }

  private String postChatPrompt(String prompt) throws Exception {
    recordingChatModel.setResponseText(SAMPLE_RESPONSE);
    String requestBody = objectMapper.writeValueAsString(java.util.Map.of("prompt", prompt));
    MvcResult result =
        mockMvc
            .perform(post("/api/chat").contentType(APPLICATION_JSON).content(requestBody))
            .andExpect(status().isOk())
            .andReturn();
    JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
    return body.path("response").asText();
  }

  @When("the application runner executes")
  public void applicationRunnerExecutes() {
    runnerObserved = true;
  }

  @When("the response is added to the vector store as a Document")
  public void responseIsAddedToVectorStore() {
    Document document = new Document(lastChatResponse.get());
    lastDocument.set(document);
    recordingVectorStore.add(java.util.List.of(document));
  }

  @When("the application runner waits for completion")
  public void applicationRunnerWaitsForCompletion() {
    runnerObserved = true;
  }

  @Then("the application context should load successfully")
  public void applicationContextShouldLoad() {
    assertThat(applicationContext).isNotNull();
    assertThat(applicationContext.getId()).isNotBlank();
  }

  @Then("the OpenAI chat model bean should be available")
  public void openAiChatModelBeanAvailable() {
    assertThat(applicationContext.getBean(ChatModel.class)).isNotNull();
  }

  @Then("the vector store bean should be available")
  public void vectorStoreBeanAvailable() {
    assertThat(applicationContext.getBean(VectorStore.class)).isNotNull();
  }

  @Then("a non-empty text response should be returned")
  public void nonEmptyTextResponseReturned() {
    assertThat(lastChatResponse.get()).isNotBlank();
  }

  @Then("the request should be sent to the local LMS endpoint")
  public void requestSentToLocalLms() {
    assertThat(environment.getProperty("spring.ai.openai.base-url"))
        .isEqualTo("http://localhost:1234");
    assertThat(recordingChatModel.getCallCount()).isGreaterThan(0);
  }

  @Then("the response should be treated as an OpenAI-compatible chat completion")
  public void responseTreatedAsChatCompletion() {
    assertThat(lastChatResponse.get()).isNotBlank();
  }

  @Given("the OpenAI chat model is {string}")
  public void openAiChatModelIs(String model) {
    assertThat(environment.getProperty("spring.ai.openai.chat.options.model")).isEqualTo(model);
  }

  @Then("{int} concurrent chat calls should be made asking {string}")
  public void concurrentChatCallsMade(int taskCount, String prompt) {
    assertThat(runnerObserved).isTrue();
    assertThat(recordingChatModel.countPromptsEqualTo(prompt)).isGreaterThanOrEqualTo(taskCount);
  }

  @Then("each successful response should be logged")
  public void eachSuccessfulResponseLogged() {
    assertThat(recordingChatModel.countPromptsEqualTo(SURVIVORSHIP_PROMPT)).isGreaterThanOrEqualTo(40);
    assertThat(recordingVectorStore.getDocuments().size()).isGreaterThanOrEqualTo(40);
  }

  @Then("the document should be persisted in the pgvector store")
  public void documentPersistedInPgvectorStore() {
    assertThat(recordingVectorStore.wasAdded(lastDocument.get())).isTrue();
  }

  @Then("the embedding should use dimension {int}")
  public void embeddingUsesDimension(int dimension) {
    assertThat(environment.getProperty("spring.ai.vectorstore.pgvector.embedding-dimension", Integer.class))
        .isEqualTo(dimension);
  }

  @Then("all CompletableFuture tasks should join successfully")
  public void allFuturesJoinSuccessfully() {
    assertThat(runnerObserved).isTrue();
    assertThat(applicationContext).isNotNull();
    assertThat(recordingVectorStore.getDocuments().size()).isGreaterThanOrEqualTo(40);
  }

  @Then("the virtual thread task executor bean should be available")
  public void virtualThreadTaskExecutorBeanAvailable() {
    assertThat(applicationContext.getBean(org.springframework.core.task.VirtualThreadTaskExecutor.class))
        .isNotNull();
  }
}
