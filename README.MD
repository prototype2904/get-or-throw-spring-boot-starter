# Get or Throw Spring boot Starter

Get or Throw Spring boot Starter will help you to hide handling if entity not found.

## 1. Setup
## 2. Usage
Library adds class `GetOrThrow` like `Optional` but with errorMessage string and `orThrow()` method 
which throw "NotFound" exception with message if `GetOrThrow` is empty.


If you have Spring-data-jpa dependency in project, get-or-throw-starter will configure converters in Jpa 
and you can use `GetOrThrow` in Jpa-repositories.

Example:
```java
interface EntityRepository extends JpaRepository<Entity, Long> {

    @NotFoundExceptionMessage("Not found entity with id = {}")
    @Query("" +
            "SELECT s " +
            "  FROM Entity s " +
            " WHERE s.id = :id ")
    GetOrThrow<Entity> findOneById(@Param("id") Long id);
}
```
Client code:
```java
    private final EntityRepository entityRepository;

    public Entity getById(Long id){
        return entityRepository.findOneById(id)
                .orThrow();
    }
```

If you have entity with id then GetOrThrow will have the entity, like Optional method orElseThrow().
If you have not entity with id then GetOrThrow will throw "Not Found" exception (default RuntimeException) with error message from 
@NotFoundExceptionMessage annotation. 

Example:
```java
entityRepository.findOneById(1) -> Entity object with id 1
entityRepository.findOneById(100) -> throw new RuntimeException("Not found entity with id = 100")
```

### NotFoundExceptionMessage
`@NotFoundExceptionMessage` adds error message in `GetOrThrow`. You can add `{}` and param value from method will be inserted in error message.

Example:
```java
@NotFoundExceptionMessage("Not found entity with id = {}")
GetOrThrow<Entity> findOneById(@Param("id") Long id);

...

entityRepository.findOneById(100)
    .orThrow();
```
equals in Optional-style
```java
        
entityRepository.findOneById(100)
    .orElseThrow(() -> new RuntimeException("Not found entity with id = 100"))
```

With `@NotFoundExceptionMessage` you can change errorMessage in one place.

You can use `GetOrThrow` without `@NotFoundExceptionMessage`. `GetOrThrow` will throw Exception with default error message `Entity not found`


### GetOrThrowExceptionFactory

In default GetOrThrow will throw RuntimeException.
You can change this behavior with GetOrThrowExceptionFactory bean.
Create your custom GetOrThrowExceptionFactory implementation and register as bean.

Example:
```java
@Bean
public GetOrThrowExceptionFactory getOrThrowExceptionFactory(){
    return new GetOrThrowExceptionFactory() {
        @Override
        public RuntimeException createException(String s) {
                return new CustomNotFoundException(s);
        }
    };
}
    
and

entityRepository.findOneById(100) -> throw new CustomNotFoundException("Not found entity with id = 100")
```



