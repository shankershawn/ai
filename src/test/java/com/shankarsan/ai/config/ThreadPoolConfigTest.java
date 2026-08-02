package com.shankarsan.ai.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.VirtualThreadTaskExecutor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ThreadPoolConfigTest {

  @Test
  void createsAndRunsVirtualThreadTaskExecutor() throws Exception {
    VirtualThreadTaskExecutor executor = new ThreadPoolConfig().virtualThreadTaskExecutor();

    CountDownLatch latch = new CountDownLatch(1);
    executor.execute(latch::countDown);

    assertThat(latch.await(2, TimeUnit.SECONDS)).isTrue();
  }
}
