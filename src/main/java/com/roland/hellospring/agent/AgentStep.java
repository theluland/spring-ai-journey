package com.roland.hellospring.agent;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Das ist das "structured output" Objekt, das der LLM liefern muss.
 * BeanOutputConverter baut dafür ein JSON-Schema und parst die Antwort in dieses Objekt.
 */
public record AgentStep(
        @JsonProperty(required = true) String action,
        @JsonProperty() String clarifyingQuestion,
        @JsonProperty() Boolean isClaim,
        @JsonProperty() String reason,
        @JsonProperty() Double confidence,
        @JsonProperty() List<String> citations,
        @JsonProperty() List<String> missingInfo
) {}
