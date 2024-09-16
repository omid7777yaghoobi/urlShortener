package com.example.metrics.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "url_data")
@Data
public class UrlMetrics {

    @Id
    private String id;
    private String shortUrl;
    private String owner;
    private int hitCount;
}
