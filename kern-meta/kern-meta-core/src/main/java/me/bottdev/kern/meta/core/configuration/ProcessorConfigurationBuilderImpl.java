package me.bottdev.kern.meta.core.configuration;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.meta.core.ProcessingContext;
import me.bottdev.kern.meta.core.configuration.bound.BoundPipeline;
import me.bottdev.kern.meta.core.configuration.bound.BoundPipelineBuilder;
import me.bottdev.kern.meta.core.configuration.bound.BoundPipelineBuilderImpl;
import me.bottdev.kern.meta.core.configuration.standalone.EmptyStandalonePipeline;
import me.bottdev.kern.meta.core.configuration.standalone.EmptyStandalonePipelineBuilder;
import me.bottdev.kern.meta.core.configuration.standalone.EmptyStandalonePipelineBuilderImpl;
import me.bottdev.kern.meta.core.configuration.standalone.StandalonePipeline;
import me.bottdev.kern.meta.core.models.Model;
import me.bottdev.kern.meta.core.models.ModelKind;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ProcessorConfigurationBuilderImpl implements ProcessorConfigurationBuilder {

    private final ProcessingContext context;
    private final List<BoundPipeline<?>> boundPipelines = new ArrayList<>();
    private final List<StandalonePipeline> afterAllPipelines = new ArrayList<>();
    private final List<StandalonePipeline> afterRoundPipelines = new ArrayList<>();

    @Override
    public <M extends Model, A extends Annotation> BoundPipelineBuilder<M> select(
            ModelKind<M> kind,
            Class<A> annotationType
    ) {

        return new BoundPipelineBuilderImpl<>(
                context,
                boundPipelines,
                kind,
                annotationType,
                model -> Stream.of(kind.cast(model))
        );

    }

    @Override
    public EmptyStandalonePipelineBuilder afterAll() {
        return new EmptyStandalonePipelineBuilderImpl(
                afterAllPipelines
        );

    }

    @Override
    public EmptyStandalonePipelineBuilder afterRound() {
        return new EmptyStandalonePipelineBuilderImpl(
                afterRoundPipelines
        );

    }

    @Override
    public ProcessorConfiguration build() {
        return new ProcessorConfiguration(
                boundPipelines,
                afterAllPipelines,
                afterRoundPipelines
        );
    }

}
