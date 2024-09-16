package com.example.metrics.service;


import com.example.metrics.model.UrlMetrics;
import com.example.metrics.repository.UrlMetricsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final UrlMetricsRepository urlMetricsRepository;
    private final ObjectMapper objectMapper;
    private final MongoTemplate mongoTemplate;

    @KafkaListener(topics = "${url-metrics.url-topic}", groupId = "my-group-id")
    public void consume(String message) {
        try {

            log.info("New Url Message Received: {}", message);

            UrlMetrics urlMetrics = objectMapper.readValue(message, UrlMetrics.class);            
            urlMetrics.setHitCount(0);
            urlMetricsRepository.save(urlMetrics);

            log.info("Saved to MongoDB: ", urlMetrics);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to process url message: {}", message);
        }
    }

    @KafkaListener(topics = "${url-metrics.metric-topic}", groupId = "my-group-id")
    public void consumeMetrics(String message) {
        try {
            log.info("New Metric Message Received: {}", message);

            incrementHitCountByShortUrl(message);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to process metric message: {}", message);
        }
    }

    private void incrementHitCountByShortUrl(String shortUrl) {
        Query query = new Query(Criteria.where("shortUrl").is(shortUrl));
        Update update = new Update().inc("hitCount", 1);

        mongoTemplate.findAndModify(query, update, UrlMetrics.class);
    }
}
