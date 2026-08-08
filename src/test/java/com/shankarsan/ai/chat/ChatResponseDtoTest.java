package com.shankarsan.ai.chat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ChatResponseDtoTest {

  @Test
  void testChatResponseDto() {
    ChatResponseDto chatResponseDto = new ChatResponseDto("test response");
    assertThat(chatResponseDto.response()).isEqualTo("test response");
  }
}
