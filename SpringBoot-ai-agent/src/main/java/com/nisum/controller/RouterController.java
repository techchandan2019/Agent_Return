/*
package com.nisum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/agent")
public class RouterController {

    private final OrderAgentService orderAgentService;
    private final ObjectMapper objectMapper;


    public RouterController(OrderAgentService orderAgentService, ObjectMapper objectMapper) {
        this.orderAgentService = orderAgentService;
        this.objectMapper = objectMapper;
    }

    public record AgentRequest(String request) {}

    public record AgentResponse(
            boolean success,
            String language,
            String intent,
            String message,
            Object data,
            List<String> missingFields
    ) {}

    @PostMapping(value ="/chat",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AgentResponse> chat(@RequestBody AgentRequest request) throws IOException {
        String response =
                orderAgentService.handleRequest(request.request());

        try {
            // Convert the AI JSON string to your Java DTO.
            AgentResponse agentResponse =
                    objectMapper.readValue(response, AgentResponse.class);

            return ResponseEntity.ok(agentResponse);

        } catch (JacksonException e) {

            AgentResponse errorResponse = new AgentResponse(
                    false,
                    "en",
                    "UNKNOWN",
                    "The AI returned an invalid JSON response.",
                    null,
                    List.of()
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}
*/
