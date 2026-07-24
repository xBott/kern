package me.bottdev.kern.meta.core.configuration;

import me.bottdev.kern.meta.core.models.ElementModel;
import me.bottdev.kern.meta.core.models.ModelKind;

public interface ProcessorConfigurationBuilder {

    <M extends ElementModel> KindStage<M> select(ModelKind<M> kind);

    ConclusionStage conclusion();

    ProcessorConfiguration build();

}
