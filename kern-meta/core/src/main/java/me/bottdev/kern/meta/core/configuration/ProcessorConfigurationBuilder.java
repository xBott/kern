package me.bottdev.kern.meta.core.configuration;

import me.bottdev.kern.meta.core.models.Model;
import me.bottdev.kern.meta.core.models.ModelKind;

public interface ProcessorConfigurationBuilder {

    <M extends Model> KindStage<M> select(ModelKind<M> kind);

    ProcessorConfiguration build();

}
