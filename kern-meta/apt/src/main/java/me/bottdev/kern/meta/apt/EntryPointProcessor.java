package me.bottdev.kern.meta.apt;

import com.google.auto.service.AutoService;
import me.bottdev.kern.meta.core.configuration.ProcessorConfigurationBuilder;
import me.bottdev.kern.meta.core.models.ModelKind;

import javax.annotation.processing.Processor;

import static me.bottdev.kern.meta.core.diagnostic.DiagnosticRuleResult.warn;

@AutoService(Processor.class)
public class EntryPointProcessor extends AbstractMetaProcessor {

    @Override
    protected void configure(ProcessorConfigurationBuilder builder) {
        builder.select(ModelKind.CLASS)
                .with(EntryPoint.class)
                .validate(rules -> rules
                        .rule(_ -> warn("Found a warning"))
                )
                .accept();

    }

}
