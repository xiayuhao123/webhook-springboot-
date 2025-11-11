package com.example.webhookbridge.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record InboundMessage(
        @NotBlank(message = "messageId is required") String messageId,
        @NotBlank(message = "conversationId is required") String conversationId,
        @Valid @NotNull(message = "sender is required") Sender sender,
        @NotBlank(message = "channel is required") String channel,
        @NotBlank(message = "text is required") String text,
        List<Attachment> attachments,
        Map<String, Object> metadata,
        OffsetDateTime receivedAt
) {

    public InboundMessage {
        attachments = attachments == null ? List.of() : List.copyOf(attachments);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
