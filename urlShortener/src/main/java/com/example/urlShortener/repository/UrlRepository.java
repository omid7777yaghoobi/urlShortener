package com.example.urlShortener.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.urlShortener.model.Url;


public interface UrlRepository extends CrudRepository<Url, Integer> {
    List<Url> findByOwner(String owner);
    Optional<Url> findByShortUrl(String short_url);
}