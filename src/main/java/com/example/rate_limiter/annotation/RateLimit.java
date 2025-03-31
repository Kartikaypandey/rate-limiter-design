package com.example.rate_limiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    String key() default "";       // Custom key (e.g., "api-v1")

    int limit() default 100;       // Requests per window

    int windowInSeconds() default 60; // Time window
}