package com.nisum.service;

import com.nisum.tools.ReturnAgentTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
public class ReturnAgentService {


    private final ChatClient chatClient;
    private final ReturnAgentTools returnAgentTools;

    public ReturnAgentService(
            ChatClient.Builder builder,
            ChatMemory chatMemory,
            ReturnAgentTools returnAgentTools) {

        this.chatClient = builder.defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build()
        ).build();
        this.returnAgentTools = returnAgentTools;
    }

    public String chat(String conversationId, String message) {

        return chatClient.prompt()
                .system("""
                    You are a helpful Return Assistant.

                    IMPORTANT ORDER ID RULE:

                    1. First check whether the customer has provided
                       an order ID.

                    2. If the order ID is missing, empty, or unclear,
                       DO NOT CALL ANY TOOL.

                    3. Immediately ask:
                       "Could you please provide your order ID?"

                    4. Only after receiving the order ID, call
                       getOrderDetails to retrieve the order.

                    5. Show the order details to the customer.

                    6. Ask for the return reason if it is missing.

                    7. Ask the customer to confirm before submitting
                       the return request.

                    8. After confirmation, call createReturnRequest
                       exactly once.

                    Never invent an order ID.
                    Never call tools with null or missing arguments.
                    Never ask the customer for product ID, customer ID,
                    price, quantity, or delivery date.
                    Never automatically retry a failed tool call.
                    Use getOrderDetails only when an order ID is provided.
                    Call getOrderDetails only once per order lookup.
                    Ask the customer for the return reason if missing.
                    Ask for confirmation before creating a return.
                    Never repeatedly call the same tool.
                    If a tool fails, explain the error instead of retrying.
                    """)
                .user(message)
                .advisors(advisor -> advisor.param(
                        ChatMemory.CONVERSATION_ID,
                        conversationId
                ))
                .tools(returnAgentTools)
                .call()
                .content();
    }
}
