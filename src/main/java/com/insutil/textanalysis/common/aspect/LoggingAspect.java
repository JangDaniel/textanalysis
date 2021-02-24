package com.insutil.textanalysis.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
@Slf4j
public class LoggingAspect {
    @Pointcut("@annotation(com.insutil.textanalysis.common.PerformanceLogging)")
    public void Async() {}

    @Around("Async()")
    public Object loggingWithWebSocket(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch stopWatch = new StopWatch();

        try {
            stopWatch.start();
            log.info(pjp.getSignature().getDeclaringTypeName());
            log.info("start - " + pjp.getSignature().getDeclaringTypeName() + " / "
                    + pjp.getSignature().getName());
            return pjp.proceed();
        } finally {
            stopWatch.stop();

            log.info("finished - " + pjp.getSignature().getDeclaringTypeName() + " / "
                    + pjp.getSignature().getName() + " time spent : " + stopWatch.getTotalTimeMillis() + " ms.");
        }
    }
}
