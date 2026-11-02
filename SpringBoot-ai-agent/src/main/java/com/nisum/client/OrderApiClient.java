package com.nisum.client;

import com.nisum.dto.OrderResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class OrderApiClient {

    private final RestClient client;

    public OrderApiClient(RestClient client) {
        this.client = client;
    }

    public OrderResponse getByOrderId(Long id) {
        try{
            return client.get()
                .uri("/orders/{id}", id)
                .retrieve()
                .body(OrderResponse.class);
            } catch (Exception e) {
                return null;
            }
    }

    public List<OrderResponse> list(String customerId) {
        return client.get()
                .uri(uri -> uri.path("/orders")
                        .queryParam("customerId", customerId)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<
                        List<OrderResponse>>() {});
    }

    public OrderResponse cancel(String id) {
        return client.post()
                .uri("/orders/{id}/cancel", id)
                .retrieve()
                .body(OrderResponse.class);
    }
}