package me.bottdev.kern.meta.core.configuration.standalone;

import me.bottdev.kern.meta.core.configuration.PipelineBuilder;
import me.bottdev.kern.meta.core.diagnostic.standalone.StandaloneDiagnosticBuilder;

import java.util.function.Consumer;

public interface StandalonePipelineBuilder extends PipelineBuilder {

    StandalonePipelineBuilder validate(Consumer<StandaloneDiagnosticBuilder> rules);

    void finishWith(Runnable runnable);

}
