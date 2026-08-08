package com.shankarsan.ai.feature.support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;

public class RecordingChatModel implements ChatModel {

  private final AtomicInteger callCount = new AtomicInteger();
  private final List<String> prompts = new CopyOnWriteArrayList<>();
  private final AtomicReference<String> responseText =
      new AtomicReference<>(
          "Survivorship bias is the logical error of concentrating on entities that passed a selection process.");

  @Override
  public ChatResponse call(Prompt prompt) {
    callCount.incrementAndGet();
    prompts.add(prompt.getContents());
    return new ChatResponse(List.of(new Generation(responseText.get())));
  }

  @Override
  public ChatOptions getDefaultOptions() {
    return OpenAiChatOptions.builder().build();
  }

  public void setResponseText(String responseText) {
    this.responseText.set(responseText);
  }

  public int getCallCount() {
    return callCount.get();
  }

  public List<String> getPrompts() {
    return new ArrayList<>(prompts);
  }

  public long countPromptsEqualTo(String prompt) {
    return prompts.stream().filter(prompt::equals).count();
  }
}
