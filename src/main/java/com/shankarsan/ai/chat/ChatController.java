package com.shankarsan.ai.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

  private final ChatModel chatModel;

  @PostMapping
  public ChatResponseDto chat(@RequestBody ChatRequest request) {
    String response = chatModel.call(request.prompt());
    return new ChatResponseDto(response);
  }
}
