package me.bottdev.kern.meta.core.configuration.standalone;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public record ResultStandalonePipeline<M>(
        Function<StandalonePipelineContext, Optional<M>> chain,
        Consumer<M> consumer,
        boolean continueProcessing
) implements StandalonePipeline {

    @Override
    public boolean run(StandalonePipelineContext context) {
        Optional<M> value = chain.apply(context);
        if (value.isEmpty()) return false;

        consumer.accept(value.get());
        return continueProcessing;
    }

}