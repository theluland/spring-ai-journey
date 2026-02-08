package com.roland.hellospring.controller;

import com.roland.hellospring.ai.ClarificationAgent;
import com.roland.hellospring.agent.AgentStep;
import com.roland.hellospring.dto.AgentStepRequest;
import org.springframework.web.bind.annotation.*;

@RestController
public class AgentController {

    private final ClarificationAgent agent;

    public AgentController(ClarificationAgent agent) {
        this.agent = agent;
    }

    @PostMapping("/agent/step")
    public AgentStep step(@RequestBody AgentStepRequest request) {
        return agent.nextStep(request.text());
    }
}
