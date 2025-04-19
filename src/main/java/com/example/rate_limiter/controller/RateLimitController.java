package com.example.rate_limiter.controller;

import com.example.rate_limiter.annotation.RateLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RateLimitController {

    @RateLimit(key = "user", limit = 5, windowInSeconds = 10)
    @GetMapping("/limited")
    public String limitedEndpoint() {
        return "This is a rate-limited endpoint";
    }

    @RateLimit(limit = 5, windowInSeconds = 30) // 5 requests per 30 seconds per UUID
    @GetMapping("/rate-limit")
    public String handleRateLimitedRequest(@RequestParam String id) {
        return "Request processed for UUID: " + id;
    }

    @GetMapping("/unlimited")
    public String unlimitedEndpoint() {
        return "This endpoint has no rate limiting";
    }
}
