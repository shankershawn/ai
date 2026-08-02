package com.shankarsan.ai.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.VirtualThreadTaskExecutor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ThreadPoolConfig.class)
class ThreadPoolConfigTest {

  @Autowired
  private VirtualThreadTaskExecutor virtualThreadTaskExecutor;

  @Test
  void executesWorkOnVirtualThreadExecutor() throws Exception {
    AtomicBoolean ran = new AtomicBoolean(false);
    CountDownLatch latch = new CountDownLatch(1);
    virtualThreadTaskExecutor.execute(
        () -> {
          ran.set(true);
          latch.countDown();
        });
    assertThat(latch.await(2, TimeUnit.SECONDS)).isTrue();
    assertThat(ran).isTrue();
  }
}
