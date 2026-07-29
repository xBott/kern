package me.bottdev.kern.meta.core.configuration.standalone;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.meta.core.MessageType;
import me.bottdev.kern.meta.core.diagnostic.DiagnosticResult;
import me.bottdev.kern.meta.core.diagnostic.standalone.StandaloneDiagnosticBuilder;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public final class EmptyStandalonePipelineBuilderImpl implements EmptyStandalonePipelineBuilder {

    private final StandaloneDiagnosticBuilder diagnosticBuilder = new StandaloneDiagnosticBuilder();
    private final List<StandalonePipeline> sink;

    @Override
    public <M> ResultStandalonePipelineBuilder<M> map(Supplier<M> supplier) {
        StandaloneDiagnosticBuilder capturedDiagnostics = this.diagnosticBuilder;

        Function<StandalonePipelineContext, Optional<M>> chain = context -> {
            DiagnosticResult result = capturedDiagnostics.build();

            if (result.hasWarnings()) {
                result.warns().forEach(warn -> context.processing().logger().message(MessageType.WARN, warn.message()));
            }

            if (result.hasErrors()) {
                result.errors().forEach(error -> context.processing().logger().message(MessageType.ERROR, error.message()));
                return Optional.empty();
            }

            return Optional.of(supplier.get());
        };

        return new ResultStandalonePipelineBuilderImpl<>(sink, chain);
    }

    @Override
    public EmptyStandalonePipelineBuilder validate(Consumer<StandaloneDiagnosticBuilder> rules) {
        rules.accept(diagnosticBuilder);
        return this;
    }

    @Override
    public void generate(Runnable runnable) {
        EmptyStandalonePipeline pipeline = new EmptyStandalonePipeline(diagnosticBuilder, runnable, true);
        sink.add(pipeline);
    }

    @Override
    public void run(Runnable runnable) {
        EmptyStandalonePipeline pipeline = new EmptyStandalonePipeline(diagnosticBuilder, runnable, false);
        sink.add(pipeline);
    }

    @Override
    public void finish() {
        run(() -> {});
    }

}
