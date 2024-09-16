package com.example.urlShortener.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.urlShortener.repository.UrlRepository;
import com.example.urlShortener.model.Url;


@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    @Transactional
    public Url createUrl(String originalUrl, String shortUrl, String owner, Boolean is_active) {
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setIsActive(is_active);
        url.setShortUrl(shortUrl);
        url.setOwner(owner);
        return urlRepository.save(url);
    }

    public List<Url> getUrlsByOwner(String owner) {
        return urlRepository.findByOwner(owner);
    }

    public boolean deactivateUrl(String shortUrl) {
        Optional<Url> urlData = urlRepository.findByShortUrl(shortUrl);
        if (urlData.isPresent()) {
            Url updatedData = urlData.get();
            updatedData.setIsActive(false);
            urlRepository.save(updatedData);
            return true;
        }
        return false;
    }
}
