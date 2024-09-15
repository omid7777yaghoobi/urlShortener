package com.example.metrics.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.metrics.model.UrlMetrics;


public interface UrlMetricsRepository extends MongoRepository<UrlMetrics, String> {
    // Custom query methods can be added here if necessary
    Optional<UrlMetrics> findByShortUrl(String short_url);
}
