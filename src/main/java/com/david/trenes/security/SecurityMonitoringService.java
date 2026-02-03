package com.david.trenes.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class SecurityMonitoringService {
    
    private final ConcurrentHashMap<String, AtomicInteger> failedLoginAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> suspiciousActivities = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> lastActivityTime = new ConcurrentHashMap<>();
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong blockedRequests = new AtomicLong(0);
    
    // Attack detection thresholds
    private static final int FAILED_LOGIN_THRESHOLD = 10;
    private static final int SUSPICIOUS_ACTIVITY_THRESHOLD = 20;
    private static final long TIME_WINDOW_MINUTES = 30;
    
    public void recordSuccessfulLogin(String clientIp, String username) {
        failedLoginAttempts.remove(clientIp);
        lastActivityTime.put(clientIp, new AtomicLong(System.currentTimeMillis()));
        log.info("Successful login - IP: {}, User: {}", clientIp, username);
    }
    
    public void recordFailedLogin(String clientIp, String username) {
        AtomicInteger count = failedLoginAttempts.computeIfAbsent(clientIp, k -> new AtomicInteger(0));
        int newCount = count.incrementAndGet();
        
        if (newCount >= FAILED_LOGIN_THRESHOLD) {
            log.warn("POTENTIAL BRUTE FORCE ATTACK DETECTED - IP: {}, Failed attempts: {}, Target user: {}", 
                    clientIp, newCount, username);
            
            // Could implement IP blocking here
            blockIpTemporarily(clientIp, "Brute force attack detected");
        }
        
        log.info("Failed login - IP: {}, User: {}, Attempts: {}", clientIp, username, newCount);
    }
    
    public void recordSuspiciousActivity(String clientIp, String activity, String details) {
        AtomicInteger count = suspiciousActivities.computeIfAbsent(clientIp, k -> new AtomicInteger(0));
        int newCount = count.incrementAndGet();
        
        if (newCount >= SUSPICIOUS_ACTIVITY_THRESHOLD) {
            log.warn("SUSPICIOUS ACTIVITY SPIKE DETECTED - IP: {}, Count: {}, Activity: {}, Details: {}", 
                    clientIp, newCount, activity, details);
            
            blockIpTemporarily(clientIp, "Suspicious activity spike");
        }
        
        log.warn("Suspicious activity - IP: {}, Activity: {}, Details: {}, Count: {}", 
                clientIp, activity, details, newCount);
    }
    
    public void recordRequest(String clientIp, String endpoint, String method) {
        totalRequests.incrementAndGet();
        lastActivityTime.put(clientIp, new AtomicLong(System.currentTimeMillis()));
        
        // Monitor for unusual patterns
        if (isUnusualPattern(clientIp, endpoint, method)) {
            recordSuspiciousActivity(clientIp, "Unusual request pattern", 
                    String.format("Endpoint: %s %s", method, endpoint));
        }
    }
    
    public void recordBlockedRequest(String clientIp, String reason) {
        blockedRequests.incrementAndGet();
        log.info("Request blocked - IP: {}, Reason: {}", clientIp, reason);
    }
    
    public void recordRateLimitExceeded(String clientIp, String username, String endpoint) {
        log.warn("Rate limit exceeded - IP: {}, User: {}, Endpoint: {}", clientIp, username, endpoint);
        recordSuspiciousActivity(clientIp, "Rate limit exceeded", 
                String.format("Endpoint: %s, User: %s", endpoint, username));
    }
    
    private boolean isUnusualPattern(String clientIp, String endpoint, String method) {
        // Implement logic to detect unusual patterns
        // For example: rapid requests to sensitive endpoints, unusual user agents, etc.
        return endpoint.contains("/admin") || endpoint.contains("/auth");
    }
    
    private void blockIpTemporarily(String clientIp, String reason) {
        log.error("TEMPORARY IP BLOCK - IP: {}, Reason: {}, Duration: {} minutes", 
                clientIp, reason, TIME_WINDOW_MINUTES);
        
        // In a real implementation, you would:
        // 1. Add the IP to a blocked list
        // 2. Set up a timer to unblock after TIME_WINDOW_MINUTES
        // 3. Possibly notify administrators
    }
    
    public SecurityMetrics getSecurityMetrics() {
        return SecurityMetrics.builder()
                .totalRequests(totalRequests.get())
                .blockedRequests(blockedRequests.get())
                .uniqueIpsWithFailedLogins(failedLoginAttempts.size())
                .uniqueIpsWithSuspiciousActivity(suspiciousActivities.size())
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public void cleanupOldRecords() {
        long currentTime = System.currentTimeMillis();
        long timeWindow = TIME_WINDOW_MINUTES * 60 * 1000;
        
        failedLoginAttempts.entrySet().removeIf(entry -> {
            AtomicLong lastActivityAtomic = lastActivityTime.get(entry.getKey());
            long lastActivity = lastActivityAtomic != null ? lastActivityAtomic.get() : 0L;
            return currentTime - lastActivity > timeWindow;
        });
        
        suspiciousActivities.entrySet().removeIf(entry -> {
            AtomicLong lastActivityAtomic = lastActivityTime.get(entry.getKey());
            long lastActivity = lastActivityAtomic != null ? lastActivityAtomic.get() : 0L;
            return currentTime - lastActivity > timeWindow;
        });
        
        lastActivityTime.entrySet().removeIf(entry -> 
                currentTime - entry.getValue().get() > timeWindow);
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SecurityMetrics {
        private long totalRequests;
        private long blockedRequests;
        private int uniqueIpsWithFailedLogins;
        private int uniqueIpsWithSuspiciousActivity;
        private LocalDateTime timestamp;
    }
}
