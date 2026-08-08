package com.shankarsan.ai.text;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shankarsan.ai.exception.ServiceException;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TextChunkingControllerTest {

  private static final String TEST_TEXT = "This is a test document.";
  private static final String TEST_QUERY = "What is this document about?";
  private static final String LLM_RESPONSE = "This document is a test.";

  @MockBean private VectorStore vectorStore;

  @MockBean private ChatModel chatModel;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void testAddDocument() throws Exception {
    mockMvc
        .perform(post("/documents").contentType(MediaType.TEXT_PLAIN).content(TEST_TEXT))
        .andExpect(status().isOk());
  }

  @Test
  void testSearch() throws Exception {
    when(vectorStore.similaritySearch(TEST_QUERY))
        .thenReturn(Collections.singletonList(new Document(TEST_TEXT)));
    when(chatModel.call(any(String.class))).thenReturn(LLM_RESPONSE);

    mockMvc
        .perform(get("/documents").param("q", TEST_QUERY))
        .andExpect(status().isOk())
        .andExpect(content().string(LLM_RESPONSE));
  }

  @Test
  void testSearchNoDocumentsFound() throws Exception {
    when(vectorStore.similaritySearch(TEST_QUERY)).thenReturn(Collections.emptyList());
    when(chatModel.call(any(String.class))).thenReturn("");

    mockMvc
        .perform(get("/documents").param("q", TEST_QUERY))
        .andExpect(status().isOk())
        .andExpect(content().string(""));
  }

  @Test
  void testAddDocumentThrowsServiceException() throws Exception {
    doThrow(new RuntimeException("Test Exception")).when(vectorStore).add(anyList());

    mockMvc
        .perform(post("/documents").contentType(MediaType.TEXT_PLAIN).content(TEST_TEXT))
        .andExpect(status().isInternalServerError())
        .andExpect(
            result ->
                assertThat(result.getResolvedException()).isInstanceOf(ServiceException.class));
  }

  @Test
  void testSearchThrowsServiceException() throws Exception {
    when(vectorStore.similaritySearch(TEST_QUERY))
        .thenThrow(new RuntimeException("Test Exception"));

    mockMvc
        .perform(get("/documents").param("q", TEST_QUERY))
        .andExpect(status().isInternalServerError())
        .andExpect(
            result ->
                assertThat(result.getResolvedException()).isInstanceOf(ServiceException.class));
  }
}
