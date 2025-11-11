package com.example.webhookbridge.support;

import com.example.webhookbridge.model.Attachment;
import com.example.webhookbridge.model.InboundMessage;
import com.example.webhookbridge.model.Sender;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MessageFormatter {

    public String buildPrompt(InboundMessage message) {
        StringBuilder builder = new StringBuilder();
        builder.append("Conversation ID: ").append(message.conversationId()).append("\n");
        builder.append("Channel: ").append(message.channel()).append("\n");
        builder.append("Sender: ").append(formatSender(message.sender())).append("\n");
        builder.append("Message: ").append(message.text()).append("\n\n");

        if (!message.attachments().isEmpty()) {
            builder.append("Attachments:\n");
            builder.append(message.attachments()
                    .stream()
                    .map(this::formatAttachment)
                    .collect(Collectors.joining("\n")));
            builder.append("\n\n");
        }

        if (!message.metadata().isEmpty()) {
            builder.append("Metadata:\n");
            message.metadata().forEach((key, value) ->
                    builder.append("- ").append(key).append(": ").append(value).append("\n"));
        }

        return builder.toString();
    }

    private String formatSender(Sender sender) {
        return sender.displayName() + " (" + sender.id() + ")" +
                Optional.ofNullable(sender.email())
                        .map(email -> " <" + email + ">")
                        .orElse("");
    }

    private String formatAttachment(Attachment attachment) {
        return "- [" + attachment.type() + "] " +
                Optional.ofNullable(attachment.name()).orElse(attachment.url()) +
                " => " + attachment.url();
    }
}
