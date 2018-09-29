package com.coder4.sbmvt.ratelimit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author coder4
 */
public class RateLimiterProvider {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private static final RateLimiterProvider instance = new RateLimiterProvider();

    private static final int CAPACITY = 2000;

    private static final int TTL_SECS = 60;

    private Cache<String, RateLimiter> rateLimiterCache;

    private RateLimiterProvider() {
        rateLimiterCache = CacheBuilder.newBuilder()
                .maximumSize(CAPACITY)
                .expireAfterAccess(TTL_SECS, TimeUnit.SECONDS)
                .build();
    }

    public static RateLimiterProvider getInstance() {
        return instance;
    }

    public Optional<RateLimiter> getRateLimiter(String key, double permitsPerSecond) {
        // 未测试线程安全，但影响不大
        try {
            return Optional.ofNullable(
                    rateLimiterCache.get(key, () -> RateLimiter.create(permitsPerSecond)));
        } catch (ExecutionException e) {
            LOG.error("getRateLimiter exception", e);
            return Optional.empty();
        }
    }

}