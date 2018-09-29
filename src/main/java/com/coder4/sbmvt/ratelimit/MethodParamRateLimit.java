package com.coder4.sbmvt.ratelimit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 根据方法+参数限流，超限会抛出HTTP 429异常
 *
 * @author coder4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface MethodParamRateLimit {

    // 每秒允许多少词请求
    double permitsPerSecond();

    // 参数下标(0开始）
    int paramIndex();
}