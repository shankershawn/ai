package com.shankarsan.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;

@Configuration
public class ThreadPoolConfig {

  @Bean
  public VirtualThreadTaskExecutor virtualThreadTaskExecutor() {
    return new VirtualThreadTaskExecutor("virtual-thread-");
  }
}
