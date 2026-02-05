package com.roland.hellospring.controller;

import com.roland.hellospring.agent.AgentOrchestrator;
import com.roland.hellospring.agent.AgentOutcome;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConversationController {

    private final AgentOrchestrator orchestrator;

    public ConversationController(AgentOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @GetMapping("/agent/run")
    public AgentOutcome run(@RequestParam String text) {

        // Demo: immer gleiche Antwort (damit du Endlosschleifen siehst & die Stops greifen)
        return orchestrator.run(text, (question) -> "Der Unfall war gestern auf dem Arbeitsweg.");
    }
}
