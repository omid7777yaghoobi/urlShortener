package com.example.metrics.controller;


import io.jsonwebtoken.Claims;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.example.metrics.model.UrlMetrics;
import com.example.metrics.repository.UrlMetricsRepository;


@Slf4j
@RestController
public class UrlMetricsController {

  @Autowired
  private UrlMetricsRepository urlMetricsRepository;

  @Value("${url-metrics.jwt.secret}")
  private String plainSecret;

  @GetMapping(path="/api/url/{urlString}/metrics")
  public ResponseEntity<?> getOriginalUrl(@PathVariable("urlString") String urlString, @RequestAttribute("claims") Claims claims) {

    try {
      String user = claims.getSubject(); 

      Optional<UrlMetrics> urlData = urlMetricsRepository.findByShortUrl(urlString);

      if (urlData.isPresent()) {
        // log.info("{} == {}", urlData.get().getOwner(), user);
        if (user.equals(urlData.get().getOwner())) {
          return ResponseEntity.ok(urlData.get());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: Getting metrics of other people is forbidden");
      }

      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Url not found"); 

    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Fetching url metrics failed");
    }

  }
}