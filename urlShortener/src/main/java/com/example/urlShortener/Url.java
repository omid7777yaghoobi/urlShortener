package com.example.urlShortener;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;


@Entity
@Table(name = "url")
public class Url {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "original_url", nullable = false, unique = true)
    private String original_url;

    @Column(name = "short_url", nullable = false, unique = true)
    private String shortUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean is_active = true;

    @Column(name = "owner", nullable = false)
    private String owner;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    public Integer getId() {
        return this.id;
    }

    // public void setId(Integer id ) {
    //     this.id = id;
    // }

    public String getOriginalUrl() {
        return this.original_url; 
    }

    public void setOriginalUrl(String original_url) {
        this.original_url = original_url;
    }

    public String getShortUrl() {
        return this.shortUrl;
    }

    public void setShortUrl(String short_url) {
        this.shortUrl = short_url;
    }

    public LocalDateTime getCreatedAt() {
        return this.created_at;
    }

    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDateTime.now();
    }
    public void setLocalDateTime(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public Boolean getIsActive() {
        return this.is_active;
    }

    public void setIsActive(Boolean is_active) {
        this.is_active = is_active;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}