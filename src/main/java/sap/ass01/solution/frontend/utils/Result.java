package sap.ass01.solution.frontend.utils;

import java.util.*;
import java.util.function.Consumer;

public class Result<R, E> {

    private final R result;
    private final Optional<E> error;

    private Result(R result, Optional<E> error) {
        this.result = result;
        this.error = error;
    }

    public static <R, E> Result<R, E> success(R result) {
        return new Result<>(result, Optional.empty());
    }

    public static <R, E> Result<R, E> failure(E error) {
        return new Result<>(null, Optional.of(error));
    }

    public void handle(Consumer<R> resultHandler, Consumer<E> errorHandler) {
        error.ifPresentOrElse(errorHandler, () -> resultHandler.accept(result));
    }
}
