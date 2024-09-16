package com.example.urlShortener.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import lombok.Data;


@Entity
@Table(name = "url")
@Data
public class Url {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "original_url", nullable = false, unique = true)
    private String originalUrl;

    @Column(name = "short_url", nullable = false, unique = true)
    private String shortUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "owner", nullable = false)
    private String owner;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDateTime.now();
    }
}