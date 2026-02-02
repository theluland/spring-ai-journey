package com.roland.hellospring.controller;

import com.roland.hellospring.ai.RagRetriever;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RagController {

   private final RagRetriever ragRetriever;

   public RagController(RagRetriever ragRetriever) {
       this.ragRetriever = ragRetriever;
   }

   @GetMapping("/rag")
   public String rag(@RequestParam String q) {
       return ragRetriever.retrieveContext(q);
   }
}
