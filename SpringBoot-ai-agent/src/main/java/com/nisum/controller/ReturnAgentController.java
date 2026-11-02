package com.nisum.controller;

import com.nisum.dto.ChatRequest;
import com.nisum.service.ReturnAgentService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
public class ReturnAgentController {

    private final ReturnAgentService returnAgentService;

    public ReturnAgentController(ReturnAgentService returnAgentService) {
        this.returnAgentService = returnAgentService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        return returnAgentService.chat(request.conversationId(), request.message());
    }
}
