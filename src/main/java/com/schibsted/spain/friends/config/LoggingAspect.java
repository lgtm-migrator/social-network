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
    public void logBeforeAllControllerMethods(JoinPoint joinPoint) {
        log.info("== Legacy Controller invoked : {} ==", joinPoint.getSignature().getName());
    }

    @After("execution(* com.schibsted.spain.friends.legacy.*.*(..))")
    public void logAfterAllControllerMethods(JoinPoint joinPoint) {
        log.info("== Legacy Controller finished : {} ==", joinPoint.getSignature().getName());
    }

    @Before("execution(* com.schibsted.spain.friends.service.*.*(..))")
    public void logBeforeAllServiceMethods(JoinPoint joinPoint) {
        log.info("== Service invoked : {} ==", joinPoint.getSignature().getName());
    }

    @After("execution(* com.schibsted.spain.friends.service.*.*(..))")
    public void logAfterAllServiceMethods(JoinPoint joinPoint) {
        log.info("==  Service finished : {} ==", joinPoint.getSignature().getName());
    }

    @Before("execution(* com.schibsted.spain.friends.legacy.*.*(..))")
    public void logBeforeAllRepositoryrMethods(JoinPoint joinPoint) {
        log.info("== Repository invoked : {} ==", joinPoint.getSignature().getName());
    }

    @After("execution(* com.schibsted.spain.friends.legacy.*.*(..))")
    public void logAfterAllRepositoryMethods(JoinPoint joinPoint) {
        log.info("== Repository finished : {} ==", joinPoint.getSignature().getName());
    }
}
