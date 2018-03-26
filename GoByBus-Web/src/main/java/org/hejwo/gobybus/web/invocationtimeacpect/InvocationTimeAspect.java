package org.hejwo.gobybus.web.invocationtimeacpect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import static java.lang.String.format;

@Aspect
@Slf4j
class InvocationTimeAspect {

    private static final int SHORT_INVOCATION_TIME = 15;

    @Pointcut("execution(* org.hejwo.gobybus.web.repositories.*Repository.*(..))")
    public void repositoriesPointcut() {
    }

    @Around("@annotation(LogInvocationTime) || @within(LogInvocationTime) || repositoriesPointcut()")
    public Object employeeAroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] params = joinPoint.getArgs();

        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            long time = end - start;

            String msg = format("Method '%s' execution lasted: %s ms. ", className + "." + methodName, time);
            String paramsMsg = getParamsMsg(params);

            logDependingOnExecutionTime(time, msg, paramsMsg);
        }
    }

    private String getParamsMsg(Object[] params) {
        String paramsMsg;
        if (params != null && params.length > 0) {
            paramsMsg = format("{\"params\": [%s]}", params);
        } else {
            paramsMsg = "";
        }
        return paramsMsg;
    }

    private void logDependingOnExecutionTime(long time, String msg, String paramsMsg) {
        if (time < SHORT_INVOCATION_TIME) {
            log.info(msg + paramsMsg);
        } else {
            String warn = format("{\"warn:\": \"Execution time > %s ms. !!!\"}", SHORT_INVOCATION_TIME);
            log.warn(msg + warn + paramsMsg);
        }
    }

}
