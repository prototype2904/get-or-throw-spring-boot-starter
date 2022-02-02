package ru.stetskevich.getorthrow;

import ru.stetskevich.getorthrow.factory.GetOrThrowExceptionFactory;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class GetOrThrow<T> {
    public static final String DEFAULT_ERROR_MESSAGE = "Не удалось найти сущность";

    private final Optional<T> value;

    private final String errorMessage;

    private final GetOrThrowExceptionFactory getOrThrowExceptionFactory;

    /**
     * Constructs an instance with the described value.
     *
     * @param value the value to describe; it's the caller's responsibility to
     *              ensure the value is non-{@code null} unless creating the singleton
     *              instance returned by {@code empty()}.
     */
    private GetOrThrow(Optional<T> value) {
        this.value = value;
        this.errorMessage = DEFAULT_ERROR_MESSAGE;
        this.getOrThrowExceptionFactory = GetOrThrowExceptionFactory.DEFAULT;
    }

    private GetOrThrow(Optional<T> value, String errorMessage) {
        this.value = value;
        this.errorMessage = errorMessage;
        this.getOrThrowExceptionFactory = GetOrThrowExceptionFactory.DEFAULT;
    }

    public GetOrThrow(Optional<T> value, String errorMessage, GetOrThrowExceptionFactory getOrThrowExceptionFactory) {
        this.value = value;
        this.errorMessage = errorMessage;
        this.getOrThrowExceptionFactory = getOrThrowExceptionFactory;
    }

    public static <T> GetOrThrow<T> empty() {
        return new GetOrThrow<>(Optional.empty());
    }

    /**
     * Returns an {@code Optional} describing the given non-{@code null}
     * value.
     *
     * @param value the value to describe, which must be non-{@code null}
     * @param <T>   the type of the value
     * @return an {@code Optional} with the value present
     * @throws NullPointerException if value is {@code null}
     */
    public static <T> GetOrThrow<T> of(T value) {
        return new GetOrThrow<>(Optional.ofNullable(value));
    }
    public static <T> GetOrThrow<T> of(T value, GetOrThrowExceptionFactory getOrThrowExceptionFactory) {
        return new GetOrThrow<>(Optional.ofNullable(value), DEFAULT_ERROR_MESSAGE, getOrThrowExceptionFactory);
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @return the non-{@code null} value described by this {@code Optional}
     * @apiNote
     */
    public T orThrow() {
        return value.orElseThrow(() -> getOrThrowExceptionFactory.createException(errorMessage));
    }

    public T orThrow(String errorMessage) {
        return changeMessage(errorMessage).orThrow();
    }

    /**
     * If a value is present, returns {@code true}, otherwise {@code false}.
     *
     * @return {@code true} if a value is present, otherwise {@code false}
     */
    public boolean isPresent() {
        return value.isPresent();
    }

    /**
     * If a value is  not present, returns {@code true}, otherwise
     * {@code false}.
     *
     * @return {@code true} if a value is not present, otherwise {@code false}
     * @since 11
     */
    public boolean isEmpty() {
        return value.isEmpty();
    }


    public GetOrThrow<T> changeMessage(String errorMessage) {
        return new GetOrThrow<>(this.value, errorMessage);
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise does nothing.
     *
     * @param action the action to be performed, if a value is present
     * @throws NullPointerException if value is present and the given action is
     *                              {@code null}
     */
    public void ifPresent(Consumer<? super T> action) {
        value.ifPresent(action);
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise performs the given empty-based action.
     *
     * @param action      the action to be performed, if a value is present
     * @param emptyAction the empty-based action to be performed, if no value is
     *                    present
     * @throws NullPointerException if a value is present and the given action
     *                              is {@code null}, or no value is present and the given empty-based
     *                              action is {@code null}.
     * @since 9
     */
    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        value.ifPresentOrElse(action, emptyAction);
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * returns an {@code Optional} describing the value, otherwise returns an
     * empty {@code Optional}.
     *
     * @param predicate the predicate to apply to a value, if present
     * @return an {@code Optional} describing the value of this
     * {@code Optional}, if a value is present and the value matches the
     * given predicate, otherwise an empty {@code Optional}
     * @throws NullPointerException if the predicate is {@code null}
     */
    public GetOrThrow<T> filter(Predicate<? super T> predicate) {
        return new GetOrThrow<>(value.filter(predicate), this.errorMessage);
    }

    /**
     * If a value is present, returns an {@code Optional} describing the result of applying the given mapping function to
     * the value, otherwise returns an empty {@code Optional}.
     *
     * <p>If the mapping function returns a {@code null} result then this method
     * returns an empty {@code Optional}.
     *
     * @param mapper the mapping function to apply to a value, if present
     * @param <U>    The type of the value returned from the mapping function
     * @return an {@code Optional} describing the result of applying a mapping
     * function to the value of this {@code Optional}, if a value is
     * present, otherwise an empty {@code Optional}
     * @throws NullPointerException if the mapping function is {@code null}
     * @apiNote This method supports post-processing on {@code Optional} values, without
     * the need to explicitly check for a return status.  For example, the
     * following code traverses a stream of URIs, selects one that has not
     * yet been processed, and creates a path from that URI, returning
     * an {@code Optional<Path>}:
     *
     * <pre>{@code
     *     Optional<Path> p =
     *         uris.stream().filter(uri -> !isProcessedYet(uri))
     *                       .findFirst()
     *                       .map(Paths::get);
     * }</pre>
     * <p>
     * Here, {@code findFirst} returns an {@code Optional<URI>}, and then
     * {@code map} returns an {@code Optional<Path>} for the desired
     * URI if one exists.
     */
    public <U> GetOrThrow<U> map(Function<? super T, ? extends U> mapper) {
        return new GetOrThrow<>(value.map(mapper), this.errorMessage);
    }

    /**
     * If a value is present, returns the result of applying the given
     * {@code Optional}-bearing mapping function to the value, otherwise returns
     * an empty {@code Optional}.
     *
     * <p>This method is similar to {@link #map(Function)}, but the mapping
     * function is one whose result is already an {@code Optional}, and if
     * invoked, {@code flatMap} does not wrap it within an additional
     * {@code Optional}.
     *
     * @param <U>    The type of value of the {@code Optional} returned by the
     *               mapping function
     * @param mapper the mapping function to apply to a value, if present
     * @return the result of applying an {@code Optional}-bearing mapping
     * function to the value of this {@code Optional}, if a value is
     * present, otherwise an empty {@code Optional}
     * @throws NullPointerException if the mapping function is {@code null} or
     *                              returns a {@code null} result
     */
    public <U> GetOrThrow<U> flatMap(Function<? super T, ? extends Optional<? extends U>> mapper) {
        return new GetOrThrow<>(value.flatMap(mapper), this.errorMessage);
    }

    /**
     * If a value is present, returns an {@code Optional} describing the value,
     * otherwise returns an {@code Optional} produced by the supplying function.
     *
     * @param supplier the supplying function that produces an {@code Optional}
     *                 to be returned
     * @return returns an {@code Optional} describing the value of this
     * {@code Optional}, if a value is present, otherwise an
     * {@code Optional} produced by the supplying function.
     * @throws NullPointerException if the supplying function is {@code null} or
     *                              produces a {@code null} result
     * @since 9
     */
    public GetOrThrow<T> or(Supplier<? extends Optional<? extends T>> supplier) {
        return new GetOrThrow<>(value.or(supplier), this.errorMessage);
    }

    /**
     * If a value is present, returns a sequential {@link Stream} containing
     * only that value, otherwise returns an empty {@code Stream}.
     *
     * @return the optional value as a {@code Stream}
     * @apiNote This method can be used to transform a {@code Stream} of optional
     * elements to a {@code Stream} of present value elements:
     * <pre>{@code
     *     Stream<Optional<T>> os = ..
     *     Stream<T> s = os.flatMap(Optional::stream)
     * }</pre>
     * @since 9
     */
    public Stream<T> stream() {
        return value.stream();
    }

    /**
     * If a value is present, returns the value, otherwise returns
     * {@code other}.
     *
     * @param other the value to be returned, if no value is present.
     *              May be {@code null}.
     * @return the value, if present, otherwise {@code other}
     */
    public T orElse(T other) {
        return value.orElse(other);
    }

    /**
     * If a value is present, returns the value, otherwise returns the result
     * produced by the supplying function.
     *
     * @param supplier the supplying function that produces a value to be returned
     * @return the value, if present, otherwise the result produced by the
     * supplying function
     * @throws NullPointerException if no value is present and the supplying
     *                              function is {@code null}
     */
    public T orElseGet(Supplier<? extends T> supplier) {
        return value.orElseGet(supplier);
    }

    /**
     * If a value is present, returns the value, otherwise throws an exception
     * produced by the exception supplying function.
     *
     * @param <X>               Type of the exception to be thrown
     * @param exceptionSupplier the supplying function that produces an
     *                          exception to be thrown
     * @return the value, if present
     * @throws X                    if no value is present
     * @throws NullPointerException if no value is present and the exception
     *                              supplying function is {@code null}
     * @apiNote A method reference to the exception constructor with an empty argument
     * list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return value.orElseThrow(exceptionSupplier);
    }

    public Optional<T> optional(){
        return value;
    }

    /**
     * Indicates whether some other object is "equal to" this {@code Optional}.
     * The other object is considered equal if:
     * <ul>
     * <li>it is also an {@code Optional} and;
     * <li>both instances have no value present or;
     * <li>the present values are "equal to" each other via {@code equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {@code true} if the other object is "equal to" this object
     * otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        return value.equals(obj);
    }

    /**
     * Returns the hash code of the value, if present, otherwise {@code 0}
     * (zero) if no value is present.
     *
     * @return hash code value of the present value or {@code 0} if no value is
     * present
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
