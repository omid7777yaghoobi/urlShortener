package com.example.urlShortener;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.urlShortener.Url;

// This will be AUTO IMPLEMENTED by Spring into a Bean called urlRepository
// CRUD refers Create, Read, Update, Delete

public interface UrlRepository extends CrudRepository<Url, Integer> {
    List<Url> findByOwner(String owner);
    List<Url> findByShortUrl(String short_url);
}