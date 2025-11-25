package org.utj.asman.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect that measures execution time of public methods in service and
 * controller packages.
 */
@Aspect
@Component
public class PerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceAspect.class);

    @Around("execution(public * org.utj.asman.service..*(..)) || execution(public * org.utj.asman.controller..*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long end = System.nanoTime();
            long durationMs = (end - start) / 1_000_000;
            String method = joinPoint.getSignature().toShortString();
            log.info("Execution time of {}: {} ms", method, durationMs);
        }
    }
}
