package com.roland.hellospring.domain;

import java.time.Instant;

public record ChatMessage(Role role, String content, Instant timestamp) {
    public ChatMessage(Role role, String content) {
        this(role, content, Instant.now());
    }

}
