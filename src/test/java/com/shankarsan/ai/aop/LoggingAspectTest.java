package com.shankarsan.ai.aop;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.shankarsan.ai.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LoggingAspectTest {

    @Autowired
    private TestService testService;

    @MockBean
    private VectorStore vectorStore;

    @MockBean
    private ChatModel chatModel;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(LoggingAspect.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void testLogMethod() {
        testService.doSomething("test");

        assertThat(listAppender.list).hasSize(2);

        ILoggingEvent entryEvent = listAppender.list.getFirst();
        assertThat(entryEvent.getLevel()).isEqualTo(Level.INFO);
        assertThat(entryEvent.getMessage()).isEqualTo("Entering method: {} with arguments: {}");
        assertThat(entryEvent.getArgumentArray()[0].toString()).contains("TestService.doSomething");
        assertThat(entryEvent.getArgumentArray()[1].toString()).isEqualTo("[test]");

        ILoggingEvent exitEvent = listAppender.list.get(1);
        assertThat(exitEvent.getLevel()).isEqualTo(Level.INFO);
        assertThat(exitEvent.getMessage()).isEqualTo("Exiting method: {} with result: {}. Execution time: {}ms");
        assertThat(exitEvent.getArgumentArray()[0].toString()).contains("TestService.doSomething");
        assertThat(exitEvent.getArgumentArray()[1].toString()).isEqualTo("Result: test");
    }

    @Test
    void testLogMethodThrowsException() {
        assertThatThrownBy(() -> testService.doSomethingThatThrows())
                .isInstanceOf(ServiceException.class);

        assertThat(listAppender.list).hasSize(2);

        ILoggingEvent entryEvent = listAppender.list.getFirst();
        assertThat(entryEvent.getLevel()).isEqualTo(Level.INFO);
        assertThat(entryEvent.getMessage()).isEqualTo("Entering method: {} with arguments: {}");
        assertThat(entryEvent.getArgumentArray()[0].toString()).contains("TestService.doSomethingThatThrows");

        ILoggingEvent exceptionEvent = listAppender.list.get(1);
        assertThat(exceptionEvent.getLevel()).isEqualTo(Level.ERROR);
        assertThat(exceptionEvent.getMessage()).isEqualTo("Exception in method: {}. Reason: {}");
        assertThat(exceptionEvent.getArgumentArray()[0].toString()).contains("TestService.doSomethingThatThrows");
        assertThat(exceptionEvent.getArgumentArray()[1].toString()).isEqualTo("Test Exception");
    }
}
