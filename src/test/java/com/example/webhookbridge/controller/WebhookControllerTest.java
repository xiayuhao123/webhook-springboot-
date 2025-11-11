package com.example.webhookbridge.controller;

import com.example.webhookbridge.model.WebhookResponse;
import com.example.webhookbridge.service.AiRelayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WebhookController.class)
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiRelayService relayService;

    @Test
    void shouldRejectInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/webhook/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }

    @Test
    void shouldRelayMessageAndReturnReply() throws Exception {
        when(relayService.handleInboundMessage(any()))
                .thenReturn(new WebhookResponse("DELIVERED", "relay-123", "hello", OffsetDateTime.now(), null));

        String payload = """
                {
                  "messageId": "msg-1",
                  "conversationId": "conv-9",
                  "channel": "im",
                  "text": "ping",
                  "sender": {
                    "id": "user-1",
                    "displayName": "Demo"
                  }
                }
                """;

        mockMvc.perform(post("/api/webhook/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"))
                .andExpect(jsonPath("$.relayId").value("relay-123"));
    }
}
