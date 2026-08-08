package com.shankarsan.ai.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shankarsan.ai.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ChatControllerTest {

  @MockBean private ChatModel chatModel;

  @MockBean private VectorStore vectorStore;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void chatReturnsModelResponse() throws Exception {
    ChatRequest chatRequest = new ChatRequest("What is survivorship bias?");
    when(chatModel.call(chatRequest.prompt())).thenReturn("A selection-bias fallacy.");

    mockMvc
        .perform(
            post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.response").value("A selection-bias fallacy."));
  }

  @Test
  void chatWrapsExceptionInServiceException() throws Exception {
    ChatRequest chatRequest = new ChatRequest("What is survivorship bias?");
    when(chatModel.call(chatRequest.prompt())).thenThrow(new RuntimeException("Test Exception"));

    mockMvc
        .perform(
            post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRequest)))
        .andExpect(status().isInternalServerError())
        .andExpect(
            result ->
                assertThat(result.getResolvedException()).isInstanceOf(ServiceException.class))
        .andExpect(jsonPath("$.message").value("An error occurred in ChatController.chat(..)"));
  }
}
