package com.example.metrics.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.metrics.model.UrlMetrics;


public interface UrlMetricsRepository extends MongoRepository<UrlMetrics, String> {
    Optional<UrlMetrics> findByShortUrl(String short_url);
}
