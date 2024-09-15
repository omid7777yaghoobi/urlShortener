package com.example.urlShortener.config;


import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.AuthorityUtils;
// import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.util.Collection;
import java.util.Date;
import javax.crypto.SecretKey;

import static java.util.stream.Collectors.joining;

import javax.crypto.spec.SecretKeySpec;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${url-shortener.jwt.secret}")
    private String plainSecret;

    // private static final String AUTHORITIES_KEY = "roles";
    private SecretKey secretKey;

    // @PostConstruct
    // public void init() {
    //     this.secretKey = new SecretKeySpec(plainSecret.getBytes(), "HmacSHA256");
    // }

    public JwtTokenProvider(@Value("${url-shortener.jwt.secret}") String plainsecret) {
        this.plainSecret = plainsecret;
        this.secretKey = new SecretKeySpec(this.plainSecret.getBytes(), "HmacSHA256");
    }

    public String getSubject(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token).getBody();
        return claims.getSubject(); 
    }

    // public Authentication getAuthentication(String token) {
    //     Claims claims = Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token).getBody();

    //     Object authoritiesClaim = claims.get(AUTHORITIES_KEY);

    //     Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null
    //             ? AuthorityUtils.NO_AUTHORITIES
    //             : AuthorityUtils
    //             .commaSeparatedStringToAuthorityList(authoritiesClaim.toString());

    //     User principal = new User(claims.getSubject(), "", authorities);

    //     return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    // }

    // public boolean validateToken(String token) {
    //     try {
    //         Claims claims = Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token).getBody();            // parseClaimsJws will check expiration date. No need do here.
    //         log.info("expiration date: {}", claims.getExpiration());
    //         return true;
    //     } catch (JwtException | IllegalArgumentException e) {
    //         log.info("Invalid JWT token: {}", e.getMessage());
    //         log.trace("Invalid JWT token trace.", e);
    //     }
    //     return false;
    // }
}