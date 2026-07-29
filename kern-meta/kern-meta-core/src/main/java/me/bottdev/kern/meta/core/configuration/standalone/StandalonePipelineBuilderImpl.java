package me.bottdev.kern.meta.core.configuration.standalone;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.meta.core.diagnostic.standalone.StandaloneDiagnosticBuilder;

import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public final class StandalonePipelineBuilderImpl implements StandalonePipelineBuilder {

    private final StandaloneDiagnosticBuilder diagnosticBuilder = new StandaloneDiagnosticBuilder();
    private final List<StandalonePipeline> sink;

    @Override
    public StandalonePipelineBuilder validate(Consumer<StandaloneDiagnosticBuilder> rules) {
        rules.accept(diagnosticBuilder);
        return this;
    }

    @Override
    public void generate(Runnable runnable) {
        StandalonePipeline pipeline = new StandalonePipeline(diagnosticBuilder, () -> {
            runnable.run();
            return true;
        });
        sink.add(pipeline);
    }

    @Override
    public void run(Runnable runnable) {
        StandalonePipeline pipeline = new StandalonePipeline(diagnosticBuilder, () -> {
            runnable.run();
            return false;
        });
        sink.add(pipeline);
    }

    @Override
    public void finish() {
        run(() -> {});
    }

}
