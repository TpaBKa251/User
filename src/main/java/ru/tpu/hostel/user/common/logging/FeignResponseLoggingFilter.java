package ru.tpu.hostel.user.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import static ru.tpu.hostel.user.common.logging.Message.FINISH_FEIGN_METHOD_EXECUTION;

@Aspect
@Component
@Slf4j
public class FeignResponseLoggingFilter {

    @Around("execution(public * ru.tpu.hostel.user.client.*Client*.*(..))")
    public Object logFeignResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String clientName = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        log.info(FINISH_FEIGN_METHOD_EXECUTION, clientName, methodName, result, duration);
        return result;
    }
}