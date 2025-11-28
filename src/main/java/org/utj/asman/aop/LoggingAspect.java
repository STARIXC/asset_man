package org.utj.asman.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Optimized logging aspect that logs only essential information
 * without dumping entire entity objects.
 * Java 8 compatible version.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final int MAX_ARG_LENGTH = 100;
    private static final int MAX_RETURN_LENGTH = 100;

    @Around("execution(public * org.utj.asman.service..*(..)) || execution(public * org.utj.asman.controller..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodSignature = joinPoint.getSignature().toShortString();
        
        // Only log in DEBUG mode to reduce noise
        if (log.isDebugEnabled()) {
            String argsInfo = formatArguments(joinPoint.getArgs());
            log.debug("-> {} {}", methodSignature, argsInfo);
        }
        
        try {
            Object result = joinPoint.proceed();
            
            // Only log return values in TRACE mode
            if (log.isTraceEnabled()) {
                String returnInfo = formatReturnValue(result);
                log.trace("<- {} returned {}", methodSignature, returnInfo);
            }
            
            return result;
        } catch (Throwable t) {
            log.error("X {} failed: {}", methodSignature, t.getMessage());
            throw t;
        }
    }

    /**
     * Format arguments in a concise way
     */
    private String formatArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return "()";
        }
        
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(formatValue(args[i]));
            
            // Limit total length
            if (sb.length() > MAX_ARG_LENGTH) {
                sb.append("...");
                break;
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Format return value in a concise way
     */
    private String formatReturnValue(Object value) {
        String formatted = formatValue(value);
        if (formatted.length() > MAX_RETURN_LENGTH) {
            return formatted.substring(0, MAX_RETURN_LENGTH) + "...";
        }
        return formatted;
    }

    /**
     * Format a single value concisely
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        
        // Handle collections
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            return String.format("[%d items]", collection.size());
        }
        
        // Handle arrays
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            return String.format("[%d items]", length);
        }
        
        // Handle strings
        if (value instanceof String) {
            String str = (String) value;
            if (str.length() > 50) {
                return str.substring(0, 47) + "...";
            }
            return str;
        }
        
        // Handle numbers, booleans, etc.
        if (isPrimitive(value)) {
            return value.toString();
        }
        
        // For entities, just show class name and ID if available
        return formatEntity(value);
    }

    /**
     * Check if value is a primitive wrapper
     */
    private boolean isPrimitive(Object value) {
        return value instanceof Number || 
               value instanceof Boolean || 
               value instanceof Character;
    }

    /**
     * Format entity objects by showing only class name and ID
     */
    private String formatEntity(Object entity) {
        String className = entity.getClass().getSimpleName();
        
        // Try to get ID using reflection
        try {
            Method idMethod = entity.getClass().getMethod("getId");
            Object id = idMethod.invoke(entity);
            return String.format("%s(id=%s)", className, id);
        } catch (NoSuchMethodException e) {
            // No getId method - just return class name
            return className;
        } catch (Exception e) {
            // Error invoking getId - just return class name
            return className;
        }
    }

    /**
     * Log exceptions with stack trace only in DEBUG mode
     */
    @AfterThrowing(
        pointcut = "execution(public * org.utj.asman.service..*(..)) || execution(public * org.utj.asman.controller..*(..))", 
        throwing = "ex"
    )
    public void logException(Throwable ex) {
        if (log.isDebugEnabled()) {
            log.debug("Exception details:", ex);
        }
    }
}