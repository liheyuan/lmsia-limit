package com.coder4.sbmvt.ratelimit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对方法限流，超限会抛出HTTP 429异常
 * @author coder4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface MethodRateLimit {

    // 每秒允许多少词请求
    double permitsPerSecond();
}