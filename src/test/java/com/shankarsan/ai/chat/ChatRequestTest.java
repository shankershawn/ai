package com.shankarsan.ai.chat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ChatRequestTest {

  @Test
  void testChatRequest() {
    ChatRequest chatRequest = new ChatRequest("test prompt");
    assertThat(chatRequest.prompt()).isEqualTo("test prompt");
  }
}
