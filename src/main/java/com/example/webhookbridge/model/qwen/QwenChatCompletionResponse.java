package com.example.webhookbridge.model.qwen;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record QwenChatCompletionResponse(
        String id,
        long created,
        String model,
        List<Choice> choices,
        Usage usage,
        QwenError error
) {

    public record Choice(
            int index,
            QwenMessage message,
            @JsonProperty("finish_reason") String finishReason
    ) {
    }

    public record Usage(
            @JsonProperty("prompt_tokens") int promptTokens,
            @JsonProperty("completion_tokens") int completionTokens,
            @JsonProperty("total_tokens") int totalTokens
    ) {
    }

    public record QwenError(
            String code,
            String message,
            @JsonProperty("request_id") String requestId
    ) {
    }
}
