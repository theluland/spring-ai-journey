package com.roland.hellospring.controller;

import com.roland.hellospring.agent.AgentOrchestrator;
import com.roland.hellospring.agent.AgentOutcome;
import com.roland.hellospring.dto.AgentRunRequest;
import org.springframework.web.bind.annotation.*;

@RestController
public class ConversationController {

    private final AgentOrchestrator orchestrator;

    public ConversationController(AgentOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/agent/run")
    public AgentOutcome run(@RequestBody AgentRunRequest request) {

        // Demo-Antwort (später ersetzen wir das durch Multi-Turn API)
        return orchestrator.run(request.text(), (question) -> "Der Unfall war gestern auf dem Arbeitsweg.");
    }
}
