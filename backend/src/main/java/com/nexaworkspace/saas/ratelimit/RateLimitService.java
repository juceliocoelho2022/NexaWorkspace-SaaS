package com.nexaworkspace.saas.ratelimit;

import com.nexaworkspace.saas.common.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService {
    private static final Logger log = LoggerFactory.getLogger(RateLimitService.class);
    private final StringRedisTemplate redis;

    public RateLimitService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void check(String scope, String identity, long limit, Duration window) {
        String key = "ratelimit:" + scope + ":" + identity;
        try {
            Long count = redis.opsForValue().increment(key);
            if (count != null && count == 1L) {
                redis.expire(key, window);
            }
            if (count != null && count > limit) {
                throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "Muitas tentativas. Aguarde e tente novamente.");
            }
        } catch (ApiException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            log.warn("Redis unavailable; rate limit skipped for scope={}: {}", scope, ex.getMessage());
        }
    }
}
