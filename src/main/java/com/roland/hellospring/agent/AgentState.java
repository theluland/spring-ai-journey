package com.roland.hellospring.agent;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AgentState {

    private final String caseText;
    private final List<String> clarifications = new ArrayList<>();
    @Setter
    private int noProgress = 0;

    public AgentState(String caseText) {
        this.caseText = caseText;
    }

    public void addClarification(String answer) {
        clarifications.add(answer);
    }

    public String combinedTextForNextStep() {
        if (clarifications.isEmpty()) {
            return caseText;
        }
        return caseText + "\n\nBISHERIGE ANTWORTEN:\n- " + String.join("\n- ", clarifications);
    }
}
