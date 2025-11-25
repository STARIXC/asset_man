package org.utj.asman.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * Logging aspect that intercepts public methods in the service and controller
 * layers.
 * It logs entry, exit (including return values) and any thrown exceptions.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // Pointcut for all public methods in service and controller packages
    @Around("execution(public * org.utj.asman.service..*(..)) || execution(public * org.utj.asman.controller..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodSignature = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        log.info("ENTER {} with args {}", methodSignature, args);
        try {
            Object result = joinPoint.proceed();
            log.info("EXIT  {} returning {}", methodSignature, result);
            return result;
        } catch (Throwable t) {
            log.error("EXCEPTION in {}: {}", methodSignature, t.getMessage(), t);
            throw t;
        }
    }

    // Separate advice to log exceptions (optional, already covered above but kept
    // for clarity)
    @AfterThrowing(pointcut = "execution(public * org.utj.asman.service..*(..)) || execution(public * org.utj.asman.controller..*(..))", throwing = "ex")
    public void logException(Throwable ex) {
        log.error("Exception caught by AOP: {}", ex.getMessage(), ex);
    }
}
