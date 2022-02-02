package ru.stetskevich.getorthrow.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.stetskevich.getorthrow.GetOrThrow;
import ru.stetskevich.getorthrow.annotation.NotFoundExceptionMessage;
import ru.stetskevich.getorthrow.factory.GetOrThrowExceptionFactory;

import java.lang.reflect.Method;

@Component
@Aspect
public class GetOrThrowAspect {

    @Autowired
    private GetOrThrowExceptionFactory getOrThrowExceptionFactory;

    @Pointcut("execution(ru.stetskevich.getorthrow.GetOrThrow<*> *.*(..))")
    public void returnGetOrThrow() {
    }

    @Around("returnGetOrThrow()")
    public Object measureMethodExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = pjp.getTarget()
                .getClass()
                .getMethod(signature.getMethod().getName(),
                        signature.getMethod().getParameterTypes());
        GetOrThrow retval = (GetOrThrow) pjp.proceed();
        retval = retval == null ? GetOrThrow.empty() : retval;
        NotFoundExceptionMessage annotation = signature.getMethod().getAnnotation(NotFoundExceptionMessage.class);
        if (annotation != null) {
            String errorMessage = annotation.value();
            for (Object arg : pjp.getArgs()) {
                errorMessage = errorMessage.replaceFirst("\\{\\}", String.valueOf(arg));
            }
            return retval.changeMessage(errorMessage);
        }
        return retval;
    }
}