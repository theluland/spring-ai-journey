package com.roland.hellospring.controller;

import com.roland.hellospring.conversation.ConversationService;
import com.roland.hellospring.dto.ChatRequest;
import com.roland.hellospring.dto.ChatResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping("/new")
    public String newConversation() {
        return conversationService.newConversationId();
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest req) {
        String conversationId = (req.conversationId() == null || req.conversationId().isBlank())
                ? conversationService.newConversationId()
                : req.conversationId();

        String answer = conversationService.chat(conversationId, req.message());
        return new ChatResponse(conversationId, answer);
    }
}
