package com.coder4.lmsia.ratelimit.aspect;

import com.coder4.lmsia.commons.http.exception.Http429TooManyRequestsException;
import com.coder4.lmsia.ratelimit.MethodRateLimit;
import com.coder4.lmsia.ratelimit.RateLimiterProvider;
import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author coder4
 */
@Component
@Aspect
public class MethodRateLimitAspect {

    protected Logger LOG = LoggerFactory.getLogger(getClass());

    @Around(value = "(execution(* com.coder4..*(..))) && @annotation(methodLimit)", argNames = "joinPoint, methodLimit")
    public Object methodAround(ProceedingJoinPoint joinPoint, MethodRateLimit methodLimit)
            throws Throwable {
        // Get RateLimiter
        Optional<RateLimiter> rateLimiterOp = RateLimiterProvider.getInstance()
                .getRateLimiter(
                        joinPoint.getSignature().toLongString(), methodLimit.permitsPerSecond());
        if (!rateLimiterOp.isPresent() || rateLimiterOp.get().tryAcquire()) {
            // allow
            return joinPoint.proceed();
        } else {
            // deny
            throw new Http429TooManyRequestsException();
        }
    }

}