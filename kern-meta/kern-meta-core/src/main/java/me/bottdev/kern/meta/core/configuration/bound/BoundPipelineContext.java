package me.bottdev.kern.meta.core.configuration.bound;

import me.bottdev.kern.meta.core.ProcessingContext;
import me.bottdev.kern.meta.core.configuration.PipelineContext;
import me.bottdev.kern.meta.core.models.Model;

public record BoundPipelineContext(
        Model model,
        ProcessingContext processing
) implements PipelineContext {}
