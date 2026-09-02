package me.bottdev.kern.meta.core.configuration.standalone;

import me.bottdev.kern.meta.core.ProcessingContext;
import me.bottdev.kern.meta.core.configuration.PipelineContext;

public record StandalonePipelineContext(
        ProcessingContext processing
) implements PipelineContext {}
