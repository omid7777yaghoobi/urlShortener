package com.example.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class UrlKafkaFilter implements WebFilter {
    @Value("${api-gateway.metrics-topic}")
    private String metricsTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public UrlKafkaFilter(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (path.startsWith("/u/")) {

            String messageKey = path.substring(3);

            // kafkaTemplate.send(metricsTopic, messageKey, null);
            kafkaTemplate.send(metricsTopic, messageKey);

            System.out.println("Sent message key to Kafka: " + messageKey);
        }

        return chain.filter(exchange);
    }
}
