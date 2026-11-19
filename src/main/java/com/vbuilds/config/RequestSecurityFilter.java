package com.vbuilds.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;

/**
 * Same-origin CSRF defense for the session-cookie API.
 *
 * All browser mutations must have an Origin (or Referer fallback) matching the
 * explicitly configured public origin. Never trust a request's Host or forwarded
 * headers as the CSRF allowlist.
 */
@Component
public class RequestSecurityFilter extends OncePerRequestFilter {
    private final String allowedOrigin;

    public RequestSecurityFilter(@Value("${vbuilds.public-origin:http://localhost:8080}") String publicOrigin) {
        String origin = publicOrigin.strip();
        URI uri;
        try {
            uri = URI.create(origin);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid vbuilds.public-origin", e);
        }
        if (!origin.equals(uri.getScheme() + "://" + uri.getRawAuthority()) ||
                !("https".equals(uri.getScheme()) || "http".equals(uri.getScheme())) ||
                uri.getHost() == null || uri.getUserInfo() != null) {
            throw new IllegalArgumentException("vbuilds.public-origin must be a single HTTP(S) origin without a path");
        }
        this.allowedOrigin = origin;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Content-Security-Policy", "frame-ancestors 'none'");
        if (request.getRequestURI().startsWith("/api/")) {
            response.setHeader("Cache-Control", "no-store");
        }

        if (request.getRequestURI().startsWith("/api/") && isUnsafe(request.getMethod()) &&
                !isSameOrigin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Cross-origin request rejected");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isUnsafe(String method) {
        return !("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method) ||
                "OPTIONS".equalsIgnoreCase(method) || "TRACE".equalsIgnoreCase(method));
    }

    private boolean isSameOrigin(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        if (origin != null) {
            return allowedOrigin.equals(origin);
        }
        String referer = request.getHeader("Referer");
        if (referer == null) return false;
        try {
            URI uri = URI.create(referer);
            return allowedOrigin.equals(uri.getScheme() + "://" + uri.getRawAuthority()) &&
                    uri.getUserInfo() == null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
