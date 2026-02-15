package com.roland.hellospring.memory;

import com.roland.hellospring.domain.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryConversationMemoryStore implements ConversationMemoryStore {

    // conversationId -> messages (chronologisch)
    private final Map<String, Deque<ChatMessage>> store = new ConcurrentHashMap<>();

    @Override
    public void append(String conversationId, ChatMessage message) {
        store.computeIfAbsent(conversationId, x -> new ArrayDeque<>()).addLast(message);
    }

    @Override
    public List<ChatMessage> history(String conversationId, int limit) {
        var deque = store.getOrDefault(conversationId, new ArrayDeque<>());
        if (limit <= 0 || deque.isEmpty()) return List.of();

        int skip = Math.max(0, deque.size() - limit);
        var result = new ArrayList<ChatMessage>(Math.min(limit, deque.size()));
        int i = 0;
        for (var msg : deque) {
            if (i++ < skip) continue;
            result.add(msg);
        }
        return result;
    }

}
