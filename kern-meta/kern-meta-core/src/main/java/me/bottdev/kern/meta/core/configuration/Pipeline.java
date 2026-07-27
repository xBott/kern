package me.bottdev.kern.meta.core.configuration;

public interface Pipeline<C extends PipelineContext> {

    void run(C context);

}
