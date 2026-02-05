package com.roland.hellospring.agent;

public record AgentOutcome(
        String status,
        AgentStep finalStep,
        int steps
) {}
