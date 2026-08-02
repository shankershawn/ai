package com.shankarsan.ai.feature.support;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class RecordingVectorStore implements VectorStore {

  private final List<Document> documents = new CopyOnWriteArrayList<>();
  private final List<List<Document>> addInvocations = new CopyOnWriteArrayList<>();

  @Override
  public void add(List<Document> documents) {
    addInvocations.add(List.copyOf(documents));
    this.documents.addAll(documents);
  }

  @Override
  public Optional<Boolean> delete(List<String> idList) {
    documents.removeIf(document -> idList.contains(document.getId()));
    return Optional.of(true);
  }

  @Override
  public List<Document> similaritySearch(SearchRequest request) {
    return documents.stream()
        .filter(document -> document.getContent() != null
            && document.getContent().contains(request.getQuery()))
        .limit(request.getTopK())
        .toList();
  }

  public List<Document> getDocuments() {
    return new ArrayList<>(documents);
  }

  public List<List<Document>> getAddInvocations() {
    return new ArrayList<>(addInvocations);
  }

  public boolean wasAdded(Document document) {
    return addInvocations.stream().anyMatch(batch -> batch.contains(document));
  }
}
