package com.nisum.client;

import com.nisum.dto.CreateReturnRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class ReturnServiceClient {

    private final RestClient restClient;

    public ReturnServiceClient(RestClient returnRestClient) {
        this.restClient = returnRestClient;
    }

    public String createReturn(CreateReturnRequest request) {

        try {
            String response = restClient.post()
                    .uri("/api/v1/returns")
                    .body(request)
                    .retrieve()
                    .body(String.class);

            return response != null ? response : "Return service returned an empty response.";

        } catch (RestClientResponseException ex) {
            return "Return request failed. HTTP status: "
                    + ex.getStatusCode()
                    + ". Details: "
                    + ex.getResponseBodyAsString();
        }
    }
}

