package com.ticketgo.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@Order(1)
@Slf4j
public class RequestLogAspect {
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object controllerLogAround(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecutionTime(joinPoint);
    }

    private Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();

        String requestPath = request.getRequestURI();
        String httpMethod = request.getMethod();

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("[RequestLog-REQUEST] {} {} - {}.{} - Input parameters: {}",
                httpMethod,
                requestPath,
                className,
                methodName,
                Arrays.toString(args));

        return joinPoint.proceed();
    }
}
