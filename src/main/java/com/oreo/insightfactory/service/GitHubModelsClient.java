package com.oreo.insightfactory.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class GitHubModelsClient {

    private final RestClient restClient;
    private final String token;
    private final String url;
    private final String modelId;

    public GitHubModelsClient(
            @Value("${app.github-models.token}") String token,
            @Value("${app.github-models.url}") String url,
            @Value("${app.github-models.model-id}") String modelId
    ) {
        this.restClient = RestClient.create();
        this.token = token;
        this.url = url;
        this.modelId = modelId;
    }

    public String summarize(String prompt) {
        if (token == null || token.isBlank()) {
            return "";
        }

        Map<String, Object> request = Map.of(
                "model", modelId,
                "temperature", 0.2,
                "max_tokens", 180,
                "messages", List.of(
                        Map.of("role", "system", "content", "Eres un analista de ventas de Oreo. Responde breve, claro y accionable en espanol."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        Map<?, ?> response = restClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(request)
                .retrieve()
                .body(Map.class);

        return extractContent(response);
    }

    private String extractContent(Map<?, ?> response) {
        if (response == null) {
            return "";
        }

        Object choicesObject = response.get("choices");
        if (!(choicesObject instanceof List<?> choices) || choices.isEmpty()) {
            return "";
        }

        Object firstChoice = choices.getFirst();
        if (!(firstChoice instanceof Map<?, ?> choice)) {
            return "";
        }

        Object messageObject = choice.get("message");
        if (!(messageObject instanceof Map<?, ?> message)) {
            return "";
        }

        Object content = message.get("content");
        return content == null ? "" : content.toString();
    }
}
