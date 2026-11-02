package com.nisum.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AIRouterConfiguration {

    @Bean
    public ChatClient getChatClient(ChatClient.Builder builder,ChatMemory chatMemory) {
        return builder.defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build()
        ).build();

    }

    @Bean
    public RestClient returnRestClient() {

        return RestClient.builder()
                .baseUrl("http://localhost:4042")
                .build();
    }
    @Bean
    public ChatMemory chatMemory() {

        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
    }

}
