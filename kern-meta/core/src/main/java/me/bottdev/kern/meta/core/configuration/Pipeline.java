package me.bottdev.kern.meta.core.configuration;

public interface Pipeline<C extends PipelineContext> {

    boolean run(C context);

}
