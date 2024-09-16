package com.example.urlShortener.controller;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import io.jsonwebtoken.Claims;
import io.seruco.encoding.base62.Base62;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.urlShortener.model.addUrlRequestBody;
import com.example.urlShortener.model.Url;
import com.example.urlShortener.model.UrlMessage;
import com.example.urlShortener.repository.UrlRepository;
import com.example.urlShortener.service.UrlService;


@Slf4j
@RestController
public class UrlController {

  @Autowired
  private UrlRepository urlRepository;

  @Value("${url-shortener.url-topic}")
  private String urlTopic;

  @Autowired
  private KafkaTemplate<String, UrlMessage> kafkaTemplate;

  @Autowired
  private UrlService urlService;


  @GetMapping(path="/u/{urlString}")
  public ResponseEntity<?> getOriginalUrl(@PathVariable("urlString") String urlString) {

    String server_addr = "127.0.0.1:8080/u/";
    Optional<Url> url_data = urlRepository.findByShortUrl(server_addr + urlString); 
    if (url_data.isPresent()) {
      String original_url = url_data.get().getOriginalUrl();

      HttpHeaders headers = new HttpHeaders();
      headers.add("Location", "http://" + original_url);
      return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Url not found"); 
  }

  @PostMapping(path="/api/url/{urlString}/deactivate")
  public ResponseEntity<?> deactivateUrl(@PathVariable("urlString") String urlString) {
    try {
      String server_addr = "127.0.0.1:8080/u/";

      boolean success = urlService.deactivateUrl(server_addr + urlString);
      if (success) {
          return ResponseEntity.ok("URL deactivated successfully.");
      }

      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Url not found");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Internal server error");
    }
  }

  @GetMapping(path="/api/url")
  public ResponseEntity<?> getUrlsByOwner(HttpServletRequest request, @RequestAttribute("claims") Claims claims) {

    try {
      String owner = claims.getSubject(); 

      List<Url> url_list = urlService.getUrlsByOwner(owner);
      return ResponseEntity.ok(url_list);
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Fetching url metrics failed");
    }
  }

  @PostMapping(path="/api/url")
  public ResponseEntity<?> createNewUrl(@RequestBody addUrlRequestBody origUrl, @RequestAttribute("claims") Claims claims) {
    String originalUrl = origUrl.getUrl();
    try {

        String owner = claims.getSubject(); 

        Base62 base62 = Base62.createInstance();
        byte[] encoded = base62.encode(originalUrl.getBytes());
        String base62Encoded = new String(encoded);
        String urlId = base62Encoded.substring(0, 7);
        String shortUrl = "127.0.0.1:8080/u/" + urlId;

        Url created_url = urlService.createUrl(origUrl.getUrl(), shortUrl, owner, true);       

        UrlMessage urlMessage = new UrlMessage(urlId, owner);
        kafkaTemplate.send(urlTopic, urlMessage);
        
        return ResponseEntity.ok(Map.of("short_url", created_url.getShortUrl()));
    } catch (DataIntegrityViolationException e) {
        String errorMessage = e.getMostSpecificCause().getMessage();

        if (errorMessage.contains(originalUrl)) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Original URL already exists.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: url Creation failed.");
    } catch (Exception e) {
        log.error("url creation failed: {}", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Url creation failed.");
    }
  }
}