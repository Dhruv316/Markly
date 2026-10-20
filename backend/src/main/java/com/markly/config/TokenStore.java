package com.markly.config;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/** Keeps issued login tokens in memory (simple and sufficient for a single-instance app). */
@Component
public class TokenStore {
    private final Map<String, String> tokens = new ConcurrentHashMap<>();

    public String issue(String username) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, username);
        return token;
    }

    public boolean isValid(String token) {
        return token != null && tokens.containsKey(token);
    }
}
