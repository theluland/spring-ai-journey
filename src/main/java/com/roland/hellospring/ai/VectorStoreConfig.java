package com.roland.hellospring.ai;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class VectorStoreConfig {

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {

        // In-Memory VectorStore
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();

        // Beispiel-Dokumente (simulieren Knowledge Base)
        List<Document> docs = List.of(
                new Document("Sachschäden am Fahrrad wie ein beschädigtes Schaltwerk sind typische Versicherungsschäden."),
                new Document("Unfälle auf dem Arbeitsweg gelten in der Schweiz als versicherter Weg (UVG)."),
                new Document("Die Schadenhöhe beeinflusst, ob eine Regulierung möglich ist.")
        );

        store.add(docs);
        return store;
    }
}
