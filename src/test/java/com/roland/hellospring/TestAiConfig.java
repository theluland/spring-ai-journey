package com.roland.hellospring;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

@TestConfiguration
public class TestAiConfig {

    @Bean
    @Primary
    EmbeddingModel testEmbeddingModel() {
        return new EmbeddingModel() {

            @Override
            public EmbeddingResponse call(@NonNull EmbeddingRequest request) {
                List<String> inputs = request.getInstructions();
                List<Embedding> results = new ArrayList<>(inputs.size());

                for (int i = 0; i < inputs.size(); i++) {
                    results.add(new Embedding(vector(inputs.get(i)), i));
                }

                return new EmbeddingResponse(results);
            }

            @Override
            public float[] embed(Document document) {
                return vector(document.getText());
            }

            @Override
            public int dimensions() {
                return 8;
            }

            private float[] vector(String s) {
                float[] v = new float[dimensions()];
                int h = (s == null) ? 0 : s.hashCode();

                for (int i = 0; i < v.length; i++) {
                    v[i] = ((h >> (i * 4)) & 0xF) / 15.0f;
                }

                return v;
            }
        };
    }
}
