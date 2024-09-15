package com.example.metrics.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.metrics.model.UrlMetrics;
import com.example.metrics.repository.UrlMetricsRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class KafkaConsumerService {

    private final UrlMetricsRepository urlMetricsRepository;
    private final ObjectMapper objectMapper;

    // @Value("${url-metrics.url-topic}")
    // private static String urlTopic;
    
    // @Value("${url-metrics.metric-topic}")
    // private static String metricTopic;
    
    
    private static final String urlTopicName = "url-tp";
    private static final String metricTopicName = "metrics-tp";


    public KafkaConsumerService(UrlMetricsRepository urlMetricsRepository, ObjectMapper objectMapper) {
        this.urlMetricsRepository = urlMetricsRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = urlTopicName, groupId = "my-group-id")
    public void consume(String message) {
        try {

            System.out.println("a new url message Received: " + message);

            // Convert the received JSON string to a UrlData object
            UrlMetrics urlMetrics = objectMapper.readValue(message, UrlMetrics.class);
            
            // Initialize the hit count
            urlMetrics.setHitCount(0);

            // Save the UrlData object to MongoDB
            urlMetricsRepository.save(urlMetrics);

            // System.out.println("Saved to MongoDB: " + urlMetrics);
            log.info("Saved to MongoDB: ", urlMetrics);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to process url message: {}", message);
            // System.err.println("Failed to process message: " + message);
        }
    }

    @KafkaListener(topics = metricTopicName, groupId = "my-group-id")
    public void consumeMetrics(String message) {
        try {

            System.out.println("a new metric message Received: " + message);

            // Convert the received JSON string to a UrlData object
            // UrlMetrics urlMetrics = objectMapper.readValue(message, UrlMetrics.class);
            
            // Initialize the hit count
            // urlMetrics.setHitCount(0);
            Optional<UrlMetrics> optionalUrlMetrics = urlMetricsRepository.findByShortUrl(message);
            if (optionalUrlMetrics.isPresent()) {
                // return found_url_list.get(0);
                UrlMetrics urlMetrics = optionalUrlMetrics.get();
                urlMetrics.setHitCount(urlMetrics.getHitCount() + 1);
                urlMetricsRepository.save(urlMetrics);
            } else {
                log.error("short url not found");
                // System.err.println("short url not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to process metric message: {}", message);
            // System.err.println("Failed to process metric message: " + message);
        }
    }
}
