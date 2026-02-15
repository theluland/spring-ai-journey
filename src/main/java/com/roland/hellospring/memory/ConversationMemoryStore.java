package com.roland.hellospring.memory;

import com.roland.hellospring.domain.ChatMessage;

import java.util.List;

public interface ConversationMemoryStore {
    void append(String conversationId, ChatMessage message);
    List<ChatMessage> history(String conversationId, int limit);
}
