package ru.stetskevich.getorthrow.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import ru.stetskevich.getorthrow.config.GetOrThrowConfig;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnMissingBean(name = "getOrThrowExceptionFactory")
public class DefaultGetOrThrowExceptionFactory implements GetOrThrowExceptionFactory<RuntimeException>{

    private static final Logger log = LoggerFactory.getLogger(DefaultGetOrThrowExceptionFactory.class);

    @PostConstruct
    public void warnInit(){
        log.warn("Warning. Using default GetOrThrowExceptionFactory");
    }

    @Override
    public RuntimeException createException(String errorMessage) {
        return new RuntimeException(errorMessage);
    }
}
