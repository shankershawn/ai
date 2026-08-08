package com.shankarsan.ai.chat;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

  @Mock private ChatModel chatModel;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new ChatController(chatModel)).build();
  }

  @Test
  void chatReturnsModelResponse() throws Exception {
    when(chatModel.call("What is survivorship bias?")).thenReturn("A selection-bias fallacy.");

    mockMvc
        .perform(
            post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"prompt\":\"What is survivorship bias?\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.response").value("A selection-bias fallacy."));

    verify(chatModel).call("What is survivorship bias?");
  }
}
