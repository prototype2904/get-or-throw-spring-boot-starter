package ru.stetskevich.getorthrow.factory;

public interface GetOrThrowExceptionFactory <E extends RuntimeException>{

    GetOrThrowExceptionFactory<RuntimeException> DEFAULT = RuntimeException::new;

    E createException(String errorMessage);
}
