package me.bottdev.kern.meta.core.configuration;

import me.bottdev.kern.meta.core.configuration.bound.BoundPipeline;
import me.bottdev.kern.meta.core.configuration.standalone.EmptyStandalonePipeline;
import me.bottdev.kern.meta.core.configuration.standalone.StandalonePipeline;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ProcessorConfiguration {

    private final List<BoundPipeline<?>> boundPipelines;
    private final List<StandalonePipeline> afterAllPipelines;
    private final List<StandalonePipeline> afterRoundPipelines;
    private final Set<String> supportedAnnotationTypes;

    public ProcessorConfiguration(
            List<BoundPipeline<?>> boundPipelines,
            List<StandalonePipeline> afterAllPipelines,
            List<StandalonePipeline> afterRoundPipelines
    ) {
        this.boundPipelines = boundPipelines;
        this.afterAllPipelines = afterAllPipelines;
        this.afterRoundPipelines = afterRoundPipelines;
        this.supportedAnnotationTypes = boundPipelines.stream().map(definition ->
                definition.annotationType().getCanonicalName()
        ).collect(Collectors.toUnmodifiableSet());
    }

    public List<BoundPipeline<?>> boundPipelines() {
        return boundPipelines;
    }

    public List<StandalonePipeline> afterAllPipelines() {
        return afterAllPipelines;
    }

    public List<StandalonePipeline> afterRoundPipelines() {
        return afterRoundPipelines;
    }

    public Set<String> supportedAnnotationTypes() {
        return supportedAnnotationTypes;
    }

}
