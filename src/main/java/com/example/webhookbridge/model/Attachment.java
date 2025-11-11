package com.example.webhookbridge.model;

import jakarta.validation.constraints.NotBlank;

public record Attachment(
        @NotBlank(message = "type is required") String type,
        @NotBlank(message = "url is required") String url,
        String name
) {
}
