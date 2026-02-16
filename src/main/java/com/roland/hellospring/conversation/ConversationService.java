package com.roland.hellospring.conversation;

import com.roland.hellospring.ai.RagRetriever;
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

import java.util.*;
import java.util.regex.Pattern;

@Service
public class ConversationService {

    private static final int HISTORY_LIMIT = 20;

    private final ConversationMemoryStore memory;
    private final ChatClient chatClient;
    private final RagRetriever ragRetriever;

    public ConversationService(ConversationMemoryStore memory, ChatClient chatClient, RagRetriever ragRetriever) {
        this.memory = memory;
        this.chatClient = chatClient;
        this.ragRetriever = ragRetriever;
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
        var history = memory.history(conversationId, HISTORY_LIMIT);

        // 3) Routing: RAG ja/nein
        boolean useRag = shouldUseRag(userMessage);

        String ragContext = null;
        if (useRag) {
            // 👉 Passe diese Zeile an dein RagRetriever-API an:
            ragContext = retrieveRagContextSafely(userMessage);
        }

        // 4) Prompt bauen
        Prompt prompt = buildPrompt(history, userMessage, ragContext);

        // 5) LLM Call
        String response = chatClient
                .prompt(prompt)
                .call()
                .content();

        // 6) ASSISTANT in Memory
        memory.append(conversationId, new ChatMessage(Role.ASSISTANT, response));

        return response;
    }

    private Prompt buildPrompt(List<ChatMessage> history, String userMessage, String ragContext) {
        var systemText = """
                Du bist ein hilfreicher AI-Coach für Software Engineering.
                Antworte präzise und praxisnah in Deutsch.
                Wenn etwas unklar ist, stelle genau eine kurze Rückfrage.
                Wenn du dir unsicher bist, sag das offen und schlage den nächsten Schritt vor.
                """;

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemText));

        // History (ohne SYSTEM, nur USER/ASSISTANT)
        for (var msg : history) {
            if (msg.role() == Role.USER) {
                messages.add(new UserMessage(msg.content()));
            } else if (msg.role() == Role.ASSISTANT) {
                messages.add(new AssistantMessage(msg.content()));
            }
        }

        // RAG Context (optional)
        if (ragContext != null && !ragContext.isBlank()) {
            var ragInstruction = """
                    Kontext aus Wissensbasis (RAG):
                    Nutze den folgenden Kontext, wenn er zur Frage passt.
                    Wenn du daraus Fakten nimmst, verweise in Klammern auf die Quelle, z.B. (Quelle [1]).
                    Wenn der Kontext nicht ausreicht, stelle eine Rückfrage statt zu raten.
                    
                    --- BEGIN CONTEXT ---
                    %s
                    --- END CONTEXT ---
                    """.formatted(ragContext);

            messages.add(new SystemMessage(ragInstruction));
        }

        // Aktuelle User Message (am Ende)
        messages.add(new UserMessage(userMessage));

        return new Prompt(messages);
    }

    private String retrieveRagContextSafely(String query) {
        try {
            // ⚠️ hier ggf. anpassen: manche Retriever geben z.B. List<Document> zurück
            // Dann würdest du joinen / formatieren.
            return ragRetriever.retrieveContext(query);
        } catch (Exception e) {
            // Fail-open: ohne RAG weiter, aber nicht crashen
            return null;
        }
    }

    // ------------------------
    // Deterministisches Routing
    // ------------------------

    private static final Pattern MEMORY_ONLY = Pattern.compile(
            "(wie hei[sß]e ich|was hast du (gerade )?gesagt|fasse zusammen|zusammenfassung|to-?dos?|schreibe um|formuliere|korrigiere)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern RAG_TRIGGERS = Pattern.compile(
            "(policy|richtlinie|regel|deckung|anspruch|prozess|dokument|handbuch|wiki|definition|bedeutet|wie funktioniert|wo steht|gem[aä][sß])",
            Pattern.CASE_INSENSITIVE
    );

    private boolean shouldUseRag(String userMessage) {
        String text = userMessage.trim();

        // 1) Wenn es eindeutig “Konversation über Konversation” ist → kein RAG
        if (MEMORY_ONLY.matcher(text).find()) return false;

        // 2) Wenn es nach Wissen/Regeln/Dokumenten klingt → RAG
        if (RAG_TRIGGERS.matcher(text).find()) return true;

        // 3) Heuristik: Fragezeichen + etwas “faktisch klingendes”
        return text.contains("?") && text.length() > 20;
    }
}
