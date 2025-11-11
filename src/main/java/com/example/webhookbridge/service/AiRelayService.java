package com.example.webhookbridge.service;

import com.example.webhookbridge.client.QwenClient;
import com.example.webhookbridge.model.InboundMessage;
import com.example.webhookbridge.model.WebhookResponse;
import com.example.webhookbridge.support.MessageFormatter;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AiRelayService {

    private final QwenClient qwenClient;
    private final MessageFormatter messageFormatter;

    public AiRelayService(QwenClient qwenClient, MessageFormatter messageFormatter) {
        this.qwenClient = qwenClient;
        this.messageFormatter = messageFormatter;
    }

    public WebhookResponse handleInboundMessage(InboundMessage inboundMessage) {
        String relayId = UUID.randomUUID().toString();
        String prompt = messageFormatter.buildPrompt(inboundMessage);
        String aiReply = qwenClient.generateReply(prompt);

        return new WebhookResponse(
                "DELIVERED",
                relayId,
                aiReply,
                OffsetDateTime.now(),
                "Message relayed to Qwen successfully."
        );
    }
}
