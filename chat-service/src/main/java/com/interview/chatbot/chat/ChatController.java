package com.interview.chatbot.chat;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:5173")
class ChatController {

    private final ChatOrchestrator orchestrator;

    ChatController(ChatOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping
    ChatResponse chat(@RequestBody ChatRequest request) {
        return orchestrator.handle(request);
    }
}
