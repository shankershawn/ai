package com.shankarsan.ai.feature;

import com.shankarsan.ai.feature.support.RecordingChatModel;
import com.shankarsan.ai.feature.support.RecordingVectorStore;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class FeatureTestConfiguration {

  @Bean
  @Primary
  RecordingChatModel recordingChatModel() {
    return new RecordingChatModel();
  }

  @Bean
  @Primary
  RecordingVectorStore recordingVectorStore() {
    return new RecordingVectorStore();
  }
}
