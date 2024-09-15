package com.example.gateway.controller;

import com.example.gateway.config.JwtTokenProvider;
import com.example.gateway.model.AuthRequest;
import com.example.gateway.model.SignupRequest;
import com.example.gateway.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.example.gateway.model.User;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final JwtTokenProvider tokenProvider;
    private final ReactiveAuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ReactiveUserDetailsService userDetailsService;  // Use ReactiveUserDetailsService


    @PostMapping("/login")
    public Mono<ResponseEntity> login(
            @RequestBody Mono<AuthRequest> authRequest) {

        return authRequest
                .flatMap(login -> this.authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(
                                login.username(), login.password()))
                        .map(tokenProvider::createToken)
                .map(jwt -> {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
                    var tokenBody = Map.of("access_token", jwt);
                    return new ResponseEntity<>(tokenBody, httpHeaders, HttpStatus.OK);
                }));
    }
    @PostMapping("/signup")
    public Mono<Object> signup(@RequestBody SignupRequest signupRequest) {
        return userRepository.findByUsername(signupRequest.username())
                .flatMap(existingUser -> Mono.error(new IllegalArgumentException("User already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    // Create a new user record with encoded password
                    User newUser = new User( UUID.randomUUID().toString(), signupRequest.username(),passwordEncoder.encode( signupRequest.password()),true, List.of("USER"));
                    return userRepository.save(newUser)
                            .map(savedUser -> "User registered successfully");
                }));
    }
    
}
