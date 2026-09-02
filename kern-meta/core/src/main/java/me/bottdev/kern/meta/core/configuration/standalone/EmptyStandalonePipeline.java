package me.bottdev.kern.meta.core.configuration.standalone;

import me.bottdev.kern.meta.core.MessageType;
import me.bottdev.kern.meta.core.diagnostic.DiagnosticResult;
import me.bottdev.kern.meta.core.diagnostic.standalone.StandaloneDiagnosticBuilder;

public record EmptyStandalonePipeline(
        StandaloneDiagnosticBuilder diagnosticBuilder,
        Runnable runnable,
        boolean continueProcessing
) implements StandalonePipeline {

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

        runnable.run();

        return continueProcessing;

    }

}
