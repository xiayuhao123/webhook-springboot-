package com.example.webhookbridge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "ai.qwen")
public record QwenClientProperties(
        String apiKey,
        String baseUrl,
        String chatPath,
        String model,
        Duration timeout,
        Double temperature,
        Double topP,
        String systemPrompt
) {

    public QwenClientProperties {
        baseUrl = baseUrl != null ? baseUrl : "https://dashscope.aliyuncs.com/compatible-mode/v1";
        chatPath = chatPath != null ? chatPath : "/chat/completions";
        model = model != null ? model : "qwen-plus";
        timeout = timeout != null ? timeout : Duration.ofSeconds(20);
        temperature = temperature != null ? temperature : 0.7d;
        topP = topP != null ? topP : 0.8d;
        systemPrompt = systemPrompt != null ? systemPrompt :
                """
                        You are an AI assistant that helps customer support agents respond to user messages.
                        Keep answers concise and actionable. If the user asks about internal data you do not have,
                        politely say you are unable to help with that specific request.
                        """;
    }
}
