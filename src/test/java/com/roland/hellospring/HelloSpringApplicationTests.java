package com.roland.hellospring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = {
        "spring.ai.openai.api-key=dummy",
        "spring.ai.model.audio.speech=none",
        "spring.ai.model.audio.transcription=none"
})
@Import(TestAiConfig.class)
class HelloSpringApplicationTests {

    @Test
    void contextLoads() {
    }

}
