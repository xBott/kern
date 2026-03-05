package me.bottdev.kern.noema.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class NoemaResult<T> {

    @Getter
    @Singular
    private final List<NoemaResponse> responses;
    private final T value;

    public boolean hasErrors() {
        return responses.stream().anyMatch(response -> response instanceof NoemaError);
    }

    public T get() {
        return value;
    }

    public Optional<T> getSafe() {
        return Optional.ofNullable(value);
    }

    public void ifSuccess(Consumer<T> consumer) {
        getSafe().ifPresent(consumer);
    }
    public void ifSuccessOrElse(Consumer<T> consumer, Consumer<List<NoemaResponse>> orElseConsumer) {
        if (hasErrors()) {
            orElseConsumer.accept(responses);
        } else {
            getSafe().ifPresent(consumer);
        }
    }

}
