package com.example.gateway.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public record User(
        @Id String id,
        String username,
        @JsonIgnore String password,
        boolean active,
        List<String> roles
) {
    public User {
        if (roles == null) {
            roles = List.of(); // Use an empty list as the default value.
        }
    }
}
