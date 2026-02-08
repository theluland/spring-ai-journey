package com.roland.hellospring.controller;

import com.roland.hellospring.ai.RagRetriever;
import com.roland.hellospring.dto.RagRequest;
import org.springframework.web.bind.annotation.*;

@RestController
public class RagController {

    private final RagRetriever ragRetriever;

    public RagController(RagRetriever ragRetriever) {
        this.ragRetriever = ragRetriever;
    }

    @PostMapping("/rag")
    public String rag(@RequestBody RagRequest request) {
        return ragRetriever.retrieveContext(request.query());
    }
}
