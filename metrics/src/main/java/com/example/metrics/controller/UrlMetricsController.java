package com.example.metrics.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.metrics.model.UrlMetrics;
import com.example.metrics.repository.UrlMetricsRepository;




import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestHeader;

// import com.example.urlShortener.config.JwtTokenProvider;
// import com.example.urlShortener.model.UrlMessage;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
// import io.seruco.encoding.base62.Base62;

// import com.example.urlShortener.Url;
import org.springframework.web.servlet.view.RedirectView;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
public class UrlMetricsController {

  @Autowired
  private UrlMetricsRepository urlMetricsRepository;

  @Value("${url-metrics.jwt.secret}")
  private String plainSecret;

  @GetMapping(path="/api/url/{urlString}/metrics")
  public ResponseEntity<?> getOriginalUrl(@PathVariable("urlString") String urlString, @RequestHeader("Authorization") String bearerToken) {

    try {
      String token = bearerToken.substring(7);
      SecretKey secretKey = new SecretKeySpec(plainSecret.getBytes(), "HmacSHA256");
      Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
      String user = claims.getSubject(); 
      System.out.println("userName: " + user);

      // List<UrlMetrics> urlDataOptional = urlMetricsRepository.findByShortUrl(urlString);
      Optional<UrlMetrics> urlData = urlMetricsRepository.findByShortUrl(urlString);

      if (urlData.isPresent()) {
        log.info("{} == {}", urlData.get().getOwner(), user);
        if (user.equals(urlData.get().getOwner())) {
          return ResponseEntity.ok(urlData.get());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: Getting metrics of other people is forbidden");
      }

      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Url not found"); 


      // String urlOwner = urlDataOptional.get(0).getOwner();
      // if (urlOwner == user) {
      //   return ResponseEntity.ok(urlDataOptional.get(0));
      // } else {
      //   return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: Getting metrics of other people is forbidden");
      // }
    } catch (Exception e) {
      log.error("Internal Server Error: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Fetching url metrics failed");
    }
    
    
    // return urlDataOptional;

    // if (urlDataOptional.isEmpty()) {
    //     // UrlData urlData = urlDataOptional.get();
    //     // return ResponseEntity.ok(urlData); // Return the data if found
    //     return ResponseEntity.notFound().build(); 
    // } else {
    //     return urlDataOptional.get(0);
    // }
  }
}