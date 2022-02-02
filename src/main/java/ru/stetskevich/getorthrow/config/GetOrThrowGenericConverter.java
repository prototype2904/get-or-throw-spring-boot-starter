package ru.stetskevich.getorthrow.config;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.stereotype.Component;
import ru.stetskevich.getorthrow.GetOrThrow;
import ru.stetskevich.getorthrow.factory.GetOrThrowExceptionFactory;

import java.util.Collections;
import java.util.Set;

@Component
public class GetOrThrowGenericConverter implements GenericConverter {

    private final GetOrThrowExceptionFactory getOrThrowExceptionFactory;

    public GetOrThrowGenericConverter(GetOrThrowExceptionFactory getOrThrowExceptionFactory) {
        this.getOrThrowExceptionFactory = getOrThrowExceptionFactory;
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Object.class, GetOrThrow.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return GetOrThrow.of(source, getOrThrowExceptionFactory);
    }
}