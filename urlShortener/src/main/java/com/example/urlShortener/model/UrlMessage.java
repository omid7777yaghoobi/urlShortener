package com.example.urlShortener.model;


import lombok.Data;


@Data
public class UrlMessage {
    private String shortUrl;
    private String owner;

    public UrlMessage(String short_url, String owner) {
        this.shortUrl = short_url;
        this.owner = owner;
    }
}
