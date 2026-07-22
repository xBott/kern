package me.bottdev.kern.meta.apt;

import com.google.auto.service.AutoService;
import me.bottdev.kern.meta.core.configuration.ProcessorConfigurationBuilder;
import me.bottdev.kern.meta.core.models.ModelKind;

import javax.annotation.processing.Processor;

@AutoService(Processor.class)
public class EntryPointProcessor extends AbstractMetaProcessor {

    @Override
    protected void configure(ProcessorConfigurationBuilder builder) {
        builder.select(ModelKind.CLASS)
                .with(EntryPoint.class)
                .peek((model, _) -> {

                    context().logger().info("Methods of class " + model.qualifiedName() + ":");
                    model.methods().forEach(methodModel -> {
                        context().logger().info(" - " + methodModel.simpleName() + " -> " + methodModel.returnType().qualifiedName());
                    });

                });
    }

}
