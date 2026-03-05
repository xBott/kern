package me.bottdev.kern.noema.result;

import java.util.ArrayList;
import java.util.List;

public class NoemaResultBuilder<T> {

    private List<NoemaResponse> responses = new ArrayList<>();
    private T value = null;

    public NoemaResultBuilder<T> value(T value) {
        this.value = value;
        return this;
    }

    public NoemaResultBuilder<T> responses(List<NoemaResponse> responses) {
        this.responses = responses;
        return this;
    }

    public NoemaResultBuilder<T> response(NoemaResponse response) {
        this.responses.add(response);
        return this;
    }

    public NoemaResult<T> build() {
        return new NoemaResult<>(responses, value);
    }

}
