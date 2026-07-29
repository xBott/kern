package me.bottdev.kern.meta.core.configuration.standalone;

import me.bottdev.kern.meta.core.configuration.PipelineBuilder;
import me.bottdev.kern.meta.core.diagnostic.standalone.StandaloneDiagnosticBuilder;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface EmptyStandalonePipelineBuilder extends PipelineBuilder {

    <M> ResultStandalonePipelineBuilder<M> map(Supplier<M> supplier);

    EmptyStandalonePipelineBuilder validate(Consumer<StandaloneDiagnosticBuilder> rules);

    void generate(Runnable runnable);

    void run(Runnable runnable);

}
