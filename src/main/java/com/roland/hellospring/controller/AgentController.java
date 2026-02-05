package com.roland.hellospring.controller;

import com.roland.hellospring.ai.ClarificationAgent;
import com.roland.hellospring.agent.AgentStep;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgentController {

    private final ClarificationAgent agent;

    public AgentController(ClarificationAgent agent) {
        this.agent = agent;
    }

    @GetMapping("/agent/step")
    public AgentStep step(@RequestParam String text) {
        return agent.nextStep(text);
    }
}
