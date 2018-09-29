package com.coder4.lmsia.ratelimit.aspect;

import com.coder4.lmsia.commons.http.exception.Http429TooManyRequestsException;
import com.coder4.lmsia.ratelimit.MethodParamRateLimit;
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
public class MethodParamRateLimitAspect {

    protected Logger LOG = LoggerFactory.getLogger(getClass());

    @Around(value = "(execution(* com.coder4..*(..))) && @annotation(methodParamLimit)", argNames = "joinPoint, methodParamLimit")
    public Object methodAround(ProceedingJoinPoint joinPoint, MethodParamRateLimit methodParamLimit)
            throws Throwable {
        // Get RateLimiter
        Optional<RateLimiter> rateLimiterOp = RateLimiterProvider.getInstance()
                .getRateLimiter(getRateLimiterKey(joinPoint, methodParamLimit), methodParamLimit.permitsPerSecond());
        if (!rateLimiterOp.isPresent() || rateLimiterOp.get().tryAcquire()) {
            // allow
            return joinPoint.proceed();
        } else {
            // deny
            throw new Http429TooManyRequestsException();
        }
    }

    private String getRateLimiterKey(ProceedingJoinPoint joinPoint, MethodParamRateLimit methodParamLimit) {

        // Get Param Value
        String paramValue = getParamLimit(joinPoint, methodParamLimit.paramIndex());

        return String.format("%s-%s", joinPoint.getSignature().toString(), paramValue);
    }

    private String getParamLimit(ProceedingJoinPoint joinPoint, int paramIndex) {
        Object[] args = joinPoint.getArgs();
        if (paramIndex < 0 || paramIndex >= args.length) {
            LOG.warn("paramIndex exceed length, use default");
            return "default_param";
        }
        return args[paramIndex].toString();
    }

}