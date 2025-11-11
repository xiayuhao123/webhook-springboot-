package com.example.webhookbridge.client;

import com.example.webhookbridge.config.QwenClientProperties;
import com.example.webhookbridge.exception.AiProviderException;
import com.example.webhookbridge.model.qwen.QwenChatCompletionRequest;
import com.example.webhookbridge.model.qwen.QwenChatCompletionResponse;
import com.example.webhookbridge.model.qwen.QwenMessage;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class QwenClient {

    private static final String PROVIDER = "Qwen3";

    private final RestTemplate restTemplate;
    private final QwenClientProperties properties;

    public QwenClient(RestTemplate qwenRestTemplate, QwenClientProperties properties) {
        this.restTemplate = qwenRestTemplate;
        this.properties = properties;
    }

    public String generateReply(String prompt) {
        QwenChatCompletionRequest request = new QwenChatCompletionRequest(
                properties.model(),
                List.of(
                        new QwenMessage("system", properties.systemPrompt()),
                        new QwenMessage("user", prompt)
                ),
                properties.temperature(),
                properties.topP(),
                Boolean.FALSE
        );

        try {
            ResponseEntity<QwenChatCompletionResponse> response =
                    restTemplate.postForEntity(properties.chatPath(), request, QwenChatCompletionResponse.class);

            QwenChatCompletionResponse body = response.getBody();
            if (body == null) {
                throw new AiProviderException("Qwen returned an empty body.", response.getStatusCode().value(), PROVIDER);
            }

            if (body.error() != null) {
                throw new AiProviderException(
                        "Qwen error: " + body.error().message(),
                        response.getStatusCode().value(),
                        PROVIDER);
            }

            return body.choices()
                    .stream()
                    .findFirst()
                    .map(choice -> choice.message().content())
                    .orElseThrow(() -> new AiProviderException(
                            "Qwen did not return any choices.",
                            response.getStatusCode().value(),
                            PROVIDER));
        } catch (HttpStatusCodeException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            throw new AiProviderException(
                    "Qwen HTTP error: " + ex.getResponseBodyAsString(),
                    statusCode.value(),
                    PROVIDER);
        } catch (Exception ex) {
            throw new AiProviderException(
                    "Unable to reach Qwen: " + ex.getMessage(),
                    500,
                    PROVIDER);
        }
    }
}
