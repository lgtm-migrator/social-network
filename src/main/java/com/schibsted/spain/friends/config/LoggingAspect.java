package com.schibsted.spain.friends.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LoggingAspect {

    @Before("execution(* com.schibsted.spain.friends.legacy.*.*(..))")
    public void logBeforeAllMethods(JoinPoint joinPoint) {
        log.info("== Legacy Controller invoked : {} ==", joinPoint.getSignature().getName());
    }

    @After("execution(* com.schibsted.spain.friends.legacy.*.*(..))")
    public void logAfterAllMethods(JoinPoint joinPoint) {
        log.info("== Legacy Controller finished : {} ==", joinPoint.getSignature().getName());
    }
}
