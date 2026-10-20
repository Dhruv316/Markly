package com.markly.controller;

import com.markly.config.TokenStore;
import com.markly.exception.ApiException;
import com.markly.model.AppUser;
import com.markly.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    public record LoginRequest(@NotBlank(message = "Enter your username") String username,
                        @NotBlank(message = "Enter your password") String password) {}

    public record LoginResponse(String token, String fullName) {}

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final TokenStore tokens;

    public AuthController(UserRepository users, PasswordEncoder encoder, TokenStore tokens) {
        this.users = users;
        this.encoder = encoder;
        this.tokens = tokens;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        AppUser user = users.findByUsername(request.username())
                .filter(u -> encoder.matches(request.password(), u.passwordHash))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Wrong username or password"));
        return new LoginResponse(tokens.issue(user.username), user.fullName);
    }
}
