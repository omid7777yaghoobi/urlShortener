package com.example.urlShortener;

import java.util.List;
import java.util.Map;
import io.jsonwebtoken.Claims;
import io.seruco.encoding.base62.Base62;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.example.urlShortener.model.UrlMessage;


@Slf4j
@RestController
public class UrlController {

  @Autowired
  private UrlRepository urlRepository;


  @Value("${url-shortener.url-topic}")
  private String urlTopic;

  @Autowired
  private KafkaTemplate<String, UrlMessage> kafkaTemplate;


  @GetMapping(path="/u/{urlString}")
  public RedirectView getOriginalUrl(@PathVariable("urlString") String urlString) {

    String server_addr = "127.0.0.1:8080/u/";
    List<Url> url_list = urlRepository.findByShortUrl(server_addr + urlString); 
    String original_url = url_list.get(0).getOriginalUrl();

    return new RedirectView("http://" + original_url);
  }

  @GetMapping(path="/api/url")
  public ResponseEntity<?> getUrlsByOwner(HttpServletRequest request, @RequestAttribute("claims") Claims claims) {

    try {
      String owner = claims.getSubject(); 

      List<Url> url_list = urlRepository.findByOwner(owner);
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
        // String shortUrl = base62Encoded.substring(0, 7);
      
        Url url = new Url();
        url.setOriginalUrl(origUrl.getUrl());
        url.setShortUrl(shortUrl);
        url.setOwner(owner);
        Url created_url = urlRepository.save(url);
        

        UrlMessage urlMessage = new UrlMessage(urlId, owner);
        kafkaTemplate.send(urlTopic, urlMessage);
        
        // return ResponseEntity.ok(created_url.getShortUrl());
        return ResponseEntity.ok(Map.of("short_url", created_url.getShortUrl()));
    } catch (DataIntegrityViolationException e) {
        String errorMessage = e.getMostSpecificCause().getMessage();

        if (errorMessage.contains(originalUrl)) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Original URL already exists.");
        }
        // Unique constraint violation - handle the duplicate password scenario
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: url Creation failed.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Url creation failed.");
    }
  }
}