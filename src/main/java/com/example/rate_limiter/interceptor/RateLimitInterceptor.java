package com.example.rate_limiter.interceptor;

import com.example.rate_limiter.annotation.RateLimit;
import com.example.rate_limiter.service.RateLimiterService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RateLimiterService rateLimiterService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);
            log.info("Rate limit 1");
            if (rateLimit != null) {

                String uuid = request.getParameter("id");
                if (uuid != null) {
                    log.info("Rate limit 2");
//                    if (!uuid.matches("[0-9a-f-]{36}")) {
//                        log.info("Rate limit 3");
//                        response.sendError(400, "Missing or invalid UUID");
//                        return false;
//                    }

                    // Enforce rate limiting using UUID as the key
                    if (!rateLimiterService.isAllowed(
                            "rate_limit:" + uuid,  // Redis key
                            rateLimit.limit(),
                            rateLimit.windowInSeconds())) {
                        log.info("Rate limit 4");

                        response.setStatus(429);
                        response.getWriter().write("Rate limit exceeded for UUID: " + uuid);
                        return false;
                    }
                } else {
                    log.info("Rate limit 5");

                    String key = StringUtils.isBlank(rateLimit.key())
                            ? request.getRemoteAddr()
                            : rateLimit.key();

                    if (!rateLimiterService.isAllowed(
                            "rate_limit:" + key,
                            rateLimit.limit(),
                            rateLimit.windowInSeconds())) {

                        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                        response.getWriter().write("Rate limit exceeded");
                        return false;
                    }
                }
            }
        }
        return true;
    }
}