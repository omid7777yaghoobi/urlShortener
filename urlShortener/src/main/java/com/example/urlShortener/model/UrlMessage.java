package com.example.urlShortener.model;


public class UrlMessage {
    private String shortUrl;
    private String owner;

    // Constructors
    public UrlMessage() {}

    public UrlMessage(String short_url, String owner) {
        this.shortUrl = short_url;
        this.owner = owner;
    }

    // Getters and Setters
    public String getShortUrl() {
        return this.shortUrl;
    }

    public void setShortUrl(String short_url) {
        this.shortUrl = short_url;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "UrlMessage{" +
                "shortUrl='" + this.shortUrl + '\'' +
                ", owner='" + this.owner + '\'' +
                '}';
    }
}
