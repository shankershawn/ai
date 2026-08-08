package com.shankarsan.ai.text;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Tag(
    name = "Document Management",
    description = "APIs for adding and searching documents in the vector store")
@Validated
public class TextChunkingController {

  private final VectorStore vectorStore;
  private final ChatModel chatModel;

  @Operation(
      summary = "Add a document to the vector store",
      description =
          "Chunks the provided text, generates embeddings, and stores them in the vector store.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Document added successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request, e.g., empty text"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @PostMapping
  public ResponseEntity<Void> addDocument(@RequestBody @NotBlank String text) {
    var tokenTextSplitter = new TokenTextSplitter();
    List<Document> documents = tokenTextSplitter.apply(List.of(new Document(text)));
    vectorStore.add(documents);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Ask a question about the documents in the vector store",
      description =
          "Performs a similarity search and uses the results to answer the question with an LLM.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully answered the question",
            content =
                @Content(
                    mediaType = "text/plain",
                    schema =
                        @Schema(
                            example =
                                "Survivorship bias is a cognitive bias that occurs when..."))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request, e.g., missing or empty query parameter"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @GetMapping
  public ResponseEntity<String> search(@RequestParam("q") @NotBlank String query) {
    List<Document> similarDocuments = vectorStore.similaritySearch(query);
    String context =
        similarDocuments.stream()
            .map(Document::getContent)
            .collect(Collectors.joining(System.lineSeparator()));

    String prompt =
        "Based on the following context, please answer the question.\n\n"
            + "Context:\n"
            + context
            + "\n\n"
            + "Question: "
            + query;

    return ResponseEntity.ok(chatModel.call(prompt));
  }
}
