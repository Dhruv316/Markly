package com.markly.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Rejects any /api request that does not carry a valid "Authorization: Bearer ..." header. */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final TokenStore tokens;

    public AuthInterceptor(TokenStore tokens) {
        this.tokens = tokens;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) return true;
        String header = req.getHeader("Authorization");
        String token = (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
        if (tokens.isValid(token)) return true;

        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.getWriter().write("{\"message\":\"Please log in to continue\"}");
        return false;
    }
}
