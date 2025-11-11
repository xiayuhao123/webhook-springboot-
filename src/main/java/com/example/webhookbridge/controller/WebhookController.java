package com.example.webhookbridge.controller;

import com.example.webhookbridge.model.InboundMessage;
import com.example.webhookbridge.model.WebhookResponse;
import com.example.webhookbridge.service.AiRelayService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    private final AiRelayService relayService;

    public WebhookController(AiRelayService relayService) {
        this.relayService = relayService;
    }

    @PostMapping("/messages")
    public ResponseEntity<WebhookResponse> onMessage(@Valid @RequestBody InboundMessage inboundMessage) {
        WebhookResponse response = relayService.handleInboundMessage(inboundMessage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("status", "ok");
    }
}
