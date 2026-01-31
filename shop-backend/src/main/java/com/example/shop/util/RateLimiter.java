package com.example.shop.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiter {

    private final Map<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    private final int maxRequests;
    private final long windowMs;

    public RateLimiter(int maxRequests, long windowMs) {
        this.maxRequests = maxRequests;
        this.windowMs = windowMs;
    }

    public boolean isAllowed(String key) {
        long now = System.currentTimeMillis();
        RateLimitInfo info = rateLimitMap.computeIfAbsent(key, k -> new RateLimitInfo(now));

        if (now - info.windowStart > windowMs) {
            info.count.set(1);
            info.windowStart = now;
            return true;
        }

        int currentCount = info.count.incrementAndGet();
        return currentCount <= maxRequests;
    }

    private static class RateLimitInfo {
        long windowStart;
        AtomicInteger count;

        RateLimitInfo(long windowStart) {
            this.windowStart = windowStart;
            this.count = new AtomicInteger(1);
        }
    }
}
