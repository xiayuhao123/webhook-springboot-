package com.example.webhookbridge.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WebhookResponse(
        String status,
        String relayId,
        String aiReply,
        OffsetDateTime respondedAt,
        String notes
) {
}
