package me.bottdev.kern.meta.apt.configuration;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.meta.core.ProcessingContext;
import me.bottdev.kern.meta.core.configuration.KindStage;
import me.bottdev.kern.meta.core.configuration.Pipeline;
import me.bottdev.kern.meta.core.configuration.ProcessorConfiguration;
import me.bottdev.kern.meta.core.configuration.ProcessorConfigurationBuilder;
import me.bottdev.kern.meta.core.models.ElementModel;
import me.bottdev.kern.meta.core.models.ModelKind;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AptProcessorConfigurationBuilder implements ProcessorConfigurationBuilder {

    private final ProcessingContext context;
    private final List<Pipeline<?>> pipelines = new ArrayList<>();

    @Override
    public <M extends ElementModel> KindStage<M> select(ModelKind<M> kind) {
        return new AptKindStage<>(context, pipelines, kind);
    }

    @Override
    public ProcessorConfiguration build() {
        return new ProcessorConfiguration(pipelines);
    }

}
