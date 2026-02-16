package com.roland.hellospring.ai;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class RagRetriever {

    private final VectorStore vectorStore;

    public RagRetriever(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String retrieveContext(String query) {
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(3)
                        .build()
        );

        return IntStream.range(0, docs.size())
                .mapToObj(i -> "[" + (i + 1) + "] " + docs.get(i).getFormattedContent())
                .collect(Collectors.joining("\n"));
    }
}
