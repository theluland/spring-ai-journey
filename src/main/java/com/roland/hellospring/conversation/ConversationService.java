package com.roland.hellospring.conversation;

import com.roland.hellospring.domain.ChatMessage;
import com.roland.hellospring.domain.Role;
import com.roland.hellospring.memory.ConversationMemoryStore;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {

    private final ConversationMemoryStore memory;
    private final ChatClient chatClient;

    public ConversationService(ConversationMemoryStore memory, ChatClient chatClient) {
        this.memory = memory;
        this.chatClient = chatClient;
    }

    public String newConversationId() {
        return UUID.randomUUID().toString();
    }

    public String chat(String conversationId, String userMessage) {
        if (conversationId == null || conversationId.isBlank()) {
            throw new IllegalArgumentException("conversationId must not be null/blank");
        }
        if (userMessage == null || userMessage.isBlank()) {
            throw new IllegalArgumentException("message must not be null/blank");
        }

        // 1) USER in Memory
        memory.append(conversationId, new ChatMessage(Role.USER, userMessage));

        // 2) History holen (Windowing)
        var history = memory.history(conversationId, 20);

        // 3) Prompt bauen: System + Verlauf
        var systemText = """
                Du bist ein hilfreicher AI-Coach für Software Engineering.
                Antworte präzise und praxisnah.
                Wenn etwas unklar ist, stelle genau eine kurze Rückfrage.
                """;

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemText));

        for (var msg : history) {
            if (msg.role() == Role.USER) {
                messages.add(new UserMessage(msg.content()));
            } else if (msg.role() == Role.ASSISTANT) {
                messages.add(new AssistantMessage(msg.content()));
            }
        }

        Prompt prompt = new Prompt(messages);

        // 4) LLM Call (Spring AI ChatClient)
        String response = chatClient
                .prompt(prompt)
                .call()
                .content();

        // 5) ASSISTANT in Memory
        memory.append(conversationId, new ChatMessage(Role.ASSISTANT, response));

        return response;
    }
}
