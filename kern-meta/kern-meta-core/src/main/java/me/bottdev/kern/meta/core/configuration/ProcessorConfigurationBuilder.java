package me.bottdev.kern.meta.core.configuration;

import me.bottdev.kern.meta.core.configuration.bound.BoundPipelineBuilder;
import me.bottdev.kern.meta.core.configuration.standalone.StandalonePipelineBuilder;
import me.bottdev.kern.meta.core.models.Model;
import me.bottdev.kern.meta.core.models.ModelKind;

import java.lang.annotation.Annotation;

public interface ProcessorConfigurationBuilder {

    <M extends Model, A extends Annotation> BoundPipelineBuilder<M> bound(
            ModelKind<M> kind,
            Class<A> annotationType
    );

    StandalonePipelineBuilder standalone();

    ProcessorConfiguration build();

}
