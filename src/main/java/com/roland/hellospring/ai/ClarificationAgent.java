package com.roland.hellospring.ai;

import com.roland.hellospring.agent.AgentStep;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Component;

@Component
public class ClarificationAgent {

    private final ChatClient chatClient;
    private final RagRetriever ragRetriever;

    // ChatClient.Builder wird durch Spring AI autokonfiguriert (via OpenAI Starter).
    public ClarificationAgent(ChatClient.Builder chatClientBuilder, RagRetriever ragRetriever) {
        this.chatClient = chatClientBuilder.build();
        this.ragRetriever = ragRetriever;
    }

    public AgentStep nextStep(String caseText) {

        // 1) RAG: Kontext holen
        String context = ragRetriever.retrieveContext(caseText);

        // 2) Structured Output Converter (Schema + Parsing)
        BeanOutputConverter<AgentStep> converter = new BeanOutputConverter<>(AgentStep.class);

        // 3) System Prompt: Regeln + Format (Schema) vorgeben
        String system = """
                Du bist ein Claims-Triage-Assistent.
                Entscheide, ob es ein Versicherungsschaden sein könnte, oder ob eine Rückfrage nötig ist.

                REGELN:
                - Antworte ausschließlich im geforderten JSON-Format.
                - action muss entweder "ask_clarifying_question" oder "final_decision" sein.
                - Wenn Kontext/Infos fehlen: action="ask_clarifying_question" und clarifyingQuestion setzen.
                - Wenn genug Infos: action="final_decision" und setze isClaim, reason, confidence.
                - citations: nenne nur Quellen, die im Kontext vorkommen (wenn vorhanden).
                - missingInfo: Liste der fehlenden Infos, wenn du nachfragst.

                JSON FORMAT (Schema/Hint):
                """ + "\n" + converter.getFormat();

        // 4) User Prompt: Fall + Kontext übergeben
        String user = """
                FALLTEXT:
                %s

                KONTEXT (RAG):
                %s
                """.formatted(caseText, context);

        // 5) LLM Call + Parse in AgentStep
        String raw = chatClient.prompt()
                .system(system)
                .user(user)
                .call()
                .content();

        return converter.convert(raw);
    }
}
