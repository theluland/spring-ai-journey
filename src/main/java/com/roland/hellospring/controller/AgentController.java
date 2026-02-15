package com.roland.hellospring.controller;

import com.roland.hellospring.agent.AgentOrchestrator;
import com.roland.hellospring.agent.AgentOutcome;
import com.roland.hellospring.dto.AgentRunRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agent")
public class AgentController {

    private final AgentOrchestrator orchestrator;

    public AgentController(AgentOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/run")
    public AgentOutcome run(@RequestBody AgentRunRequest request) {

        // Demo-Antwort (später ersetzen wir das durch echtes Tool/Memory)
        return orchestrator.run(request.text(), (question) -> "Der Unfall war gestern auf dem Arbeitsweg.");
    }
}
