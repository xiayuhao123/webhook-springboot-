package com.example.webhookbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WebhookBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebhookBridgeApplication.class, args);
    }
}
