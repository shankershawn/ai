package com.shankarsan.ai;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.core.task.VirtualThreadTaskExecutor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiApplicationTests {

  private static final String PROMPT = "What is survivorship bias?";
  private static final String RESPONSE = "A selection-bias fallacy.";

  @Mock
  private ChatModel chatModel;

  @Mock
  private VectorStore vectorStore;

  @Mock
  private VirtualThreadTaskExecutor virtualThreadTaskExecutor;

  @InjectMocks
  private AiApplication aiApplication;

  @Test
  void applicationRunnerSubmitsFortyChatAndStoreTasks() throws Exception {
    when(chatModel.call(PROMPT)).thenReturn(RESPONSE);
    doAnswer(
            invocation -> {
              Runnable task = invocation.getArgument(0);
              task.run();
              return null;
            })
        .when(virtualThreadTaskExecutor)
        .execute(any(Runnable.class));

    aiApplication.applicationRunner().run(new DefaultApplicationArguments());

    verify(chatModel, times(40)).call(PROMPT);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Document>> documentsCaptor = ArgumentCaptor.forClass(List.class);
    verify(vectorStore, times(40)).add(documentsCaptor.capture());
    assertThat(documentsCaptor.getAllValues())
        .hasSize(40)
        .allSatisfy(docs -> assertThat(docs).singleElement().extracting(Document::getContent).isEqualTo(RESPONSE));
  }
}
