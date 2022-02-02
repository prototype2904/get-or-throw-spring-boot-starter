package ru.stetskevich.getorthrow.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.jpa.repository.query.JpaQueryExecution;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.util.QueryExecutionConverters;
import org.springframework.data.util.NullableWrapper;
import ru.stetskevich.getorthrow.GetOrThrow;
import ru.stetskevich.getorthrow.factory.GetOrThrowExceptionFactory;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {GetOrThrowConfig.BASE_PACKAGE})
@Import({GetOrThrowConfig.MongoConfig.class, GetOrThrowConfig.JpaConfig.class})
public class GetOrThrowConfig {

    public static final String BASE_PACKAGE = "ru.stetskevich.getorthrow";
    private static final Logger log = LoggerFactory.getLogger(GetOrThrowConfig.class);


    @Configuration
    @ConditionalOnClass(MongoCustomConversions.class)
    public static class MongoConfig{

        @Autowired
        private GetOrThrowExceptionFactory getOrThrowExceptionFactory;

        @PostConstruct
        public void init() {
            try {
                Field WRAPPER_TYPES = QueryExecutionConverters.class.getDeclaredField("WRAPPER_TYPES");
                WRAPPER_TYPES.setAccessible(true);
                ((Set<QueryExecutionConverters.WrapperType>) WRAPPER_TYPES.get(null)).add(QueryExecutionConverters.WrapperType.singleValue(GetOrThrow.class));
                Field conversion_service = RepositoryFactorySupport.class.getDeclaredField("CONVERSION_SERVICE");
                conversion_service.setAccessible(true);
                ((GenericConversionService) conversion_service.get(null)).addConverter(new GetOrThrowConverter(getOrThrowExceptionFactory));
                log.info("GetOrThrow config for MongoDB was initilized");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Bean
        public MongoCustomConversions customConversions() {
            List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
            converterList.add(new GetOrThrowConverter(getOrThrowExceptionFactory));
            return new MongoCustomConversions(converterList);
        }
    }

    @Configuration
    @ConditionalOnClass(JpaQueryExecution.class)
    public static class JpaConfig{
        @Autowired
        private GetOrThrowExceptionFactory getOrThrowExceptionFactory;

        @PostConstruct
        public void init() {
            try {
                Field conversion_service = JpaQueryExecution.class.getDeclaredField("CONVERSION_SERVICE");
                conversion_service.setAccessible(true);
                ((DefaultConversionService) conversion_service.get(null)).addConverter(new GetOrThrowGenericConverter(getOrThrowExceptionFactory));
                log.info("GetOrThrow config for Jpa was initilized");
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }

    }

    @ReadingConverter
    static class GetOrThrowConverter implements Converter<Object, GetOrThrow> {

        private final GetOrThrowExceptionFactory getOrThrowExceptionFactory;

        public GetOrThrowConverter(GetOrThrowExceptionFactory getOrThrowExceptionFactory) {
            this.getOrThrowExceptionFactory = getOrThrowExceptionFactory;
        }

        @Override
        public GetOrThrow convert(Object source) {
            NullableWrapper wrapper = (NullableWrapper) source;
            Object value = wrapper.getValue();

            return GetOrThrow.of(value, getOrThrowExceptionFactory);
        }

    }
}