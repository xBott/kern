package me.bottdev.kern.meta.core;

import me.bottdev.kern.meta.core.models.ModelRegistry;

public record ProcessingContext(
        Logger logger,
        FileFactory fileFactory,
        ModelRegistry modelRegistry
) {
}
