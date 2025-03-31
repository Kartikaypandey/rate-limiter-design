package com.example.rate_limiter.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RateLimiterService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String key, int limit, int windowInSeconds) {
        long currentTime = System.currentTimeMillis() / 1000;
        long windowStart = currentTime - windowInSeconds;

        // Remove expired requests
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

        // Get current request count
        Long count = redisTemplate.opsForZSet().zCard(key);

        if (count != null && count >= limit) {
            return false;
        }

        // Add current request
        redisTemplate.opsForZSet().add(key, UUID.randomUUID().toString(), currentTime);
        redisTemplate.expire(key, windowInSeconds, TimeUnit.SECONDS);

        return true;
    }
}
