package me.bottdev.kern.meta.core.configuration;

import me.bottdev.kern.meta.core.diagnostic.builders.StaticDiagnosticBuilder;

import java.util.function.Consumer;

public interface ConclusionStage {

    ConclusionStage validate(Consumer<StaticDiagnosticBuilder> rules);

    void accept(Runnable runnable);

    default void accept() {
        accept(() -> {});
    }

}
