package com.example.webhookbridge.model.qwen;

import java.util.List;

public record QwenChatCompletionRequest(
        String model,
        List<QwenMessage> messages,
        Double temperature,
        Double topP,
        Boolean stream
) {
}
