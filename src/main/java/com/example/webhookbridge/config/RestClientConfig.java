package com.example.webhookbridge.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate qwenRestTemplate(RestTemplateBuilder builder, QwenClientProperties properties) {
        ClientHttpRequestInterceptor interceptor = authInterceptor(properties);
        return builder
                .rootUri(properties.baseUrl())
                .setConnectTimeout(properties.timeout())
                .setReadTimeout(properties.timeout())
                .additionalInterceptors(interceptor)
                .build();
    }

    private ClientHttpRequestInterceptor authInterceptor(QwenClientProperties properties) {
        return (request, body, execution) -> {
            if (StringUtils.hasText(properties.apiKey())) {
                request.getHeaders().setBearerAuth(properties.apiKey());
            }
            request.getHeaders().set("Content-Type", "application/json");
            request.getHeaders().set("Accept", "application/json");
            return execution.execute(request, body);
        };
    }
}
