package me.bottdev.kern.meta.core;

import me.bottdev.kern.meta.core.configuration.ProcessorConfiguration;

public interface ProcessingRound {

    void run(ProcessorConfiguration configuration, ProcessingContext context);

}
