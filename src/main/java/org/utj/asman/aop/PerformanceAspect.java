package org.utj.asman.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Optimized performance aspect that only logs slow methods
 * to reduce log noise.
 * Java 8 compatible version.
 */
@Aspect
@Component
public class PerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceAspect.class);
    
    // Only log methods that take longer than this threshold (in milliseconds)
    private static final long SLOW_METHOD_THRESHOLD_MS = 100;
    
    // Always log controller methods (user-facing), but only if slow
    private static final long CONTROLLER_THRESHOLD_MS = 200;

    @Around("execution(public * org.utj.asman.service..*(..)) || execution(public * org.utj.asman.controller..*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        // Skip performance logging if not in DEBUG mode
        if (!log.isDebugEnabled()) {
            return joinPoint.proceed();
        }
        
        long start = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long end = System.nanoTime();
            long durationMs = (end - start) / 1_000_000L;
            
            String method = joinPoint.getSignature().toShortString();
            boolean isController = method.contains("Controller.");
            
            // Determine threshold based on method type
            long threshold = isController ? CONTROLLER_THRESHOLD_MS : SLOW_METHOD_THRESHOLD_MS;
            
            // Only log if method is slow
            if (durationMs >= threshold) {
                log.warn("[SLOW] {} took {} ms", method, durationMs);
            } else if (log.isTraceEnabled()) {
                // Log all timings in TRACE mode for detailed profiling
                log.trace("[PERF] {} took {} ms", method, durationMs);
            }
        }
    }
}