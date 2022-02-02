package ru.stetskevich.getorthrow.annotation;

import ru.stetskevich.getorthrow.GetOrThrow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotFoundExceptionMessage {

    String value() default GetOrThrow.DEFAULT_ERROR_MESSAGE;
}
