package com.example.shop.config;

import com.example.shop.util.RateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimiter loginRateLimiter;
    private final RateLimiter orderRateLimiter;
    private final RateLimiter generalRateLimiter;

    public RateLimitingFilter() {
        this.loginRateLimiter = new RateLimiter(5, 60000); // 5 requests per minute
        this.orderRateLimiter = new RateLimiter(10, 60000); // 10 requests per minute
        this.generalRateLimiter = new RateLimiter(100, 60000); // 100 requests per minute
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String clientId = getClientId(request);

        RateLimiter limiter = null;

        if (path.equals("/auth/login")) {
            limiter = loginRateLimiter;
        } else if (path.equals("/api/orders") && request.getMethod().equals("POST")) {
            limiter = orderRateLimiter;
        } else if (path.startsWith("/api/")) {
            limiter = generalRateLimiter;
        }

        if (limiter != null && !limiter.isAllowed(clientId)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}");
            response.setContentType("application/json");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (token.length() >= 10) {
                return token.substring(0, 10);
            }
            return token;
        }
        return request.getRemoteAddr();
    }
}
