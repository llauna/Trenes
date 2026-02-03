package com.david.trenes.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final SecurityMonitoringService securityMonitoringService;
    
    public RateLimitingFilter(SecurityMonitoringService securityMonitoringService) {
        this.securityMonitoringService = securityMonitoringService;
    }
    
    // Rate limits by IP
    private static final int MAX_REQUESTS_PER_MINUTE_IP = 60;
    private static final int MAX_LOGIN_ATTEMPTS_PER_MINUTE_IP = 5;
    
    // Rate limits by authenticated user (more restrictive)
    private static final int MAX_REQUESTS_PER_MINUTE_USER = 120;
    private static final int MAX_BILLETE_OPERATIONS_PER_MINUTE = 20;
    
    private final ConcurrentHashMap<String, AtomicInteger> ipRequestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> ipLoginCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> userRequestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> userBilleteCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastResetTime = new ConcurrentHashMap<>();
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIp(request);
        String requestUri = request.getRequestURI();
        String username = getAuthenticatedUsername();
        
        // Record the request for monitoring
        securityMonitoringService.recordRequest(clientIp, requestUri, request.getMethod());
        
        // Reset counters if needed (every minute)
        long currentTime = System.currentTimeMillis();
        lastResetTime.compute(clientIp, (key, lastTime) -> {
            if (lastTime == null || currentTime - lastTime > 60000) {
                ipRequestCounts.remove(clientIp);
                ipLoginCounts.remove(clientIp);
                if (username != null) {
                    userRequestCounts.remove(username);
                    userBilleteCounts.remove(username);
                }
                return currentTime;
            }
            return lastTime;
        });
        
        // Check rate limits
        RateLimitResult rateLimitResult = checkRateLimits(clientIp, username, requestUri);
        
        if (rateLimitResult.isLimited()) {
            log.warn("Rate limit exceeded for IP: {}, User: {}, URI: {}, Reason: {}", 
                    clientIp, username, requestUri, rateLimitResult.getReason());
            
            // Record rate limit exceeded for monitoring
            securityMonitoringService.recordRateLimitExceeded(clientIp, username, requestUri);
            securityMonitoringService.recordBlockedRequest(clientIp, rateLimitResult.getReason());
            
            response.setStatus(429); // Too Many Requests
            response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimitResult.getLimit()));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() + 60000));
            response.setHeader("Retry-After", "60");
            response.setContentType("application/json");
            response.getWriter().write(String.format(
                "{\"error\": \"Rate limit exceeded\", \"message\": \"%s\", \"retryAfter\": 60}", 
                rateLimitResult.getMessage()
            ));
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    private RateLimitResult checkRateLimits(String clientIp, String username, String requestUri) {
        AtomicInteger ipRequestCount = ipRequestCounts.computeIfAbsent(clientIp, k -> new AtomicInteger(0));
        AtomicInteger ipLoginCount = ipLoginCounts.computeIfAbsent(clientIp, k -> new AtomicInteger(0));
        
        // Check IP-based rate limits
        if (ipRequestCount.incrementAndGet() > MAX_REQUESTS_PER_MINUTE_IP) {
            return RateLimitResult.builder()
                    .limited(true)
                    .reason("IP general limit")
                    .message("Too many requests from this IP")
                    .limit(MAX_REQUESTS_PER_MINUTE_IP)
                    .build();
        }
        
        // Check stricter limit for login endpoint
        if (requestUri.contains("/auth/login")) {
            if (ipLoginCount.incrementAndGet() > MAX_LOGIN_ATTEMPTS_PER_MINUTE_IP) {
                return RateLimitResult.builder()
                        .limited(true)
                        .reason("IP login limit")
                        .message("Too many login attempts from this IP")
                        .limit(MAX_LOGIN_ATTEMPTS_PER_MINUTE_IP)
                        .build();
            }
        }
        
        // Check user-based rate limits (only for authenticated users)
        if (username != null && !username.equals("anonymousUser")) {
            AtomicInteger userRequestCount = userRequestCounts.computeIfAbsent(username, k -> new AtomicInteger(0));
            AtomicInteger userBilleteCount = userBilleteCounts.computeIfAbsent(username, k -> new AtomicInteger(0));
            
            // General user rate limit
            if (userRequestCount.incrementAndGet() > MAX_REQUESTS_PER_MINUTE_USER) {
                return RateLimitResult.builder()
                        .limited(true)
                        .reason("User general limit")
                        .message("Too many requests for this user")
                        .limit(MAX_REQUESTS_PER_MINUTE_USER)
                        .build();
            }
            
            // Stricter limit for billete operations
            if (requestUri.contains("/billetes/")) {
                if (userBilleteCount.incrementAndGet() > MAX_BILLETE_OPERATIONS_PER_MINUTE) {
                    return RateLimitResult.builder()
                            .limited(true)
                            .reason("User billete operations limit")
                            .message("Too many billete operations for this user")
                            .limit(MAX_BILLETE_OPERATIONS_PER_MINUTE)
                            .build();
                }
            }
        }
        
        return RateLimitResult.builder().limited(false).build();
    }
    
    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
            return auth.getName();
        }
        return null;
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class RateLimitResult {
        private boolean limited;
        private String reason;
        private String message;
        private int limit;
    }
}
