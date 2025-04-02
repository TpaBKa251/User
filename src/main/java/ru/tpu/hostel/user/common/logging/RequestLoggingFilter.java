package ru.tpu.hostel.user.common.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static ru.tpu.hostel.user.common.logging.Message.START_CONTROLLER_METHOD_EXECUTION;
import static ru.tpu.hostel.user.common.logging.Message.START_FEIGN_METHOD_EXECUTION;

/**
 * Аспект для логирования запросов от клиента
 */
@Aspect
@Component
@Slf4j
public class RequestLoggingFilter {

    @Around("execution(public * ru.tpu.hostel.user.controller.*Controller*.*(..)) || " +
            "execution(public * ru.tpu.hostel.user.client.*Client*.*(..))")
    public Object logControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        if (request != null) {
            logRequest(request);
        }
        else {
            logFeignRequest(joinPoint);
        }

        return joinPoint.proceed();
    }

    private void logRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();

        log.info(START_CONTROLLER_METHOD_EXECUTION, method, requestURI);
    }

    private void logFeignRequest(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?> clazz = methodSignature.getDeclaringType();
        String clientName = clazz.getSimpleName();
        String methodName = methodSignature.getName();

        log.info(START_FEIGN_METHOD_EXECUTION, clientName, methodName);
    }
}
