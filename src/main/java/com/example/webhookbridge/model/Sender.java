package com.example.webhookbridge.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record Sender(
        @NotBlank(message = "sender id is required") String id,
        @NotBlank(message = "displayName is required") String displayName,
        @Email(message = "email must be valid") String email
) {
}
