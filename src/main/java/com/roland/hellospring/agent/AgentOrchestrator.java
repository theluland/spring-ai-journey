package com.roland.hellospring.agent;

import com.roland.hellospring.ai.ClarificationAgent;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class AgentOrchestrator {

    private final ClarificationAgent agent;

    public AgentOrchestrator(ClarificationAgent agent) {
        this.agent = agent;
    }

    /**
     * Harness/Orchestrator:
     * - ruft den Agenten in einer Schleife auf
     * - sammelt Clarifications
     * - stoppt bei final_decision oder wenn kein Fortschritt
     */
    public AgentOutcome run(String caseText, Function<String, String> answerProvider) {

        AgentState state = new AgentState(caseText);

        final int MAX_STEPS = 10;
        AgentStep lastStep = null;

        for (int step = 1; step <= MAX_STEPS; step++) {

            int prevLen = state.getClarifications().size();

            // Agent bekommt Case + bisherige Antworten
            AgentStep decision = agent.nextStep(state.combinedTextForNextStep());
            lastStep = decision;

            // STOP: final
            if ("final_decision".equals(decision.action())) {
                return new AgentOutcome("DONE", decision, step);
            }

            // Agent fragt nach
            if ("ask_clarifying_question".equals(decision.action())) {

                String question = decision.clarifyingQuestion();
                if (question == null || question.isBlank()) {
                    return new AgentOutcome("STOPPED_INVALID_STEP", decision, step);
                }

                // "Tool" / Input: answerProvider liefert Antwort (kann UI, Konsole, etc. sein)
                String answer = answerProvider.apply(question);

                // wenn answerProvider nichts liefert -> stoppen
                if (answer == null || answer.isBlank()) {
                    return new AgentOutcome("STOPPED_NO_ANSWER", decision, step);
                }

                state.addClarification(answer);
            } else {
                // unbekannte Action
                return new AgentOutcome("STOPPED_UNKNOWN_ACTION", decision, step);
            }

            // Progress-Check
            if (state.getClarifications().size() == prevLen) {
                state.setNoProgress(state.getNoProgress() + 1);
            } else {
                state.setNoProgress(0);
            }

            if (state.getNoProgress() >= 2) {
                return new AgentOutcome("STOPPED_NO_PROGRESS", lastStep, step);
            }
        }

        return new AgentOutcome("STOPPED_MAX_STEPS", lastStep, MAX_STEPS);
    }
}
