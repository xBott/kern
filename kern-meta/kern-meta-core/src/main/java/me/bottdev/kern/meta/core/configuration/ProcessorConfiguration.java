package me.bottdev.kern.meta.core.configuration;

import me.bottdev.kern.meta.core.configuration.bound.BoundPipeline;
import me.bottdev.kern.meta.core.configuration.standalone.StandalonePipeline;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ProcessorConfiguration {

    private final List<BoundPipeline<?>> boundPipelines;
    private final List<StandalonePipeline> standalonePipelines;
    private final Set<String> supportedAnnotationTypes;

    public ProcessorConfiguration(
            List<BoundPipeline<?>> boundPipelines,
            List<StandalonePipeline> standalonePipelines
    ) {
        this.boundPipelines = boundPipelines;
        this.standalonePipelines = standalonePipelines;
        this.supportedAnnotationTypes = boundPipelines.stream().map(definition ->
                definition.annotationType().getCanonicalName()
        ).collect(Collectors.toUnmodifiableSet());
    }

    public List<BoundPipeline<?>> boundPipelines() {
        return boundPipelines;
    }

    public List<StandalonePipeline> standalonePipelines() {
        return standalonePipelines;
    }

    public Set<String> supportedAnnotationTypes() {
        return supportedAnnotationTypes;
    }

}
