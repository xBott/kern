package me.bottdev.kern.meta.core.configuration.standalone;

import me.bottdev.kern.meta.core.MessageType;
import me.bottdev.kern.meta.core.configuration.Pipeline;
import me.bottdev.kern.meta.core.diagnostic.DiagnosticResult;
import me.bottdev.kern.meta.core.diagnostic.standalone.StandaloneDiagnosticBuilder;

import java.util.function.Supplier;

public record StandalonePipeline(
        StandaloneDiagnosticBuilder diagnosticBuilder,
        Supplier<Boolean> supplier
) implements Pipeline<StandalonePipelineContext> {

    @Override
    public boolean run(StandalonePipelineContext context) {
        DiagnosticResult result = diagnosticBuilder.build();

        if (result.hasWarnings()) {
            result.warns().forEach(warn -> context.processing().logger().message(MessageType.WARN, warn.message()));
        }

        if (result.hasErrors()) {
            result.errors().forEach(error -> context.processing().logger().message(MessageType.ERROR, error.message()));
            return false;
        }

        return supplier.get();

    }

}
