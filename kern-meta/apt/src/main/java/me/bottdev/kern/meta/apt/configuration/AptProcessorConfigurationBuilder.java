package me.bottdev.kern.meta.apt.configuration;

import me.bottdev.kern.meta.core.configuration.KindStage;
import me.bottdev.kern.meta.core.configuration.Pipeline;
import me.bottdev.kern.meta.core.configuration.ProcessorConfiguration;
import me.bottdev.kern.meta.core.configuration.ProcessorConfigurationBuilder;
import me.bottdev.kern.meta.core.models.Model;
import me.bottdev.kern.meta.core.models.ModelKind;

import java.util.ArrayList;
import java.util.List;

public class AptProcessorConfigurationBuilder implements ProcessorConfigurationBuilder {

    private final List<Pipeline<?, ?>> pipelines = new ArrayList<>();

    @Override
    public <M extends Model> KindStage<M> select(ModelKind<M> kind) {
        return new AptKindStage<>(pipelines, kind);
    }

    @Override
    public ProcessorConfiguration build() {
        return new ProcessorConfiguration(pipelines);
    }

}
