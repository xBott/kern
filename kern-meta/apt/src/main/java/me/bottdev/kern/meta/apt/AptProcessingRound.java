package me.bottdev.kern.meta.apt;

import me.bottdev.kern.meta.core.ProcessingContext;
import me.bottdev.kern.meta.core.ProcessingRound;
import me.bottdev.kern.meta.core.configuration.Pipeline;
import me.bottdev.kern.meta.core.configuration.ProcessorConfiguration;
import me.bottdev.kern.meta.core.models.Model;
import me.bottdev.kern.meta.core.models.ModelFactory;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import java.util.Optional;

public record AptProcessingRound(
        ModelFactory<Element> modelFactory,
        RoundEnvironment environment
) implements ProcessingRound {

    @Override
    public void run(ProcessorConfiguration configuration, ProcessingContext context) {

        for (Pipeline<?, ?> pipeline : configuration.definitions()) {

            for (Element element : environment.getElementsAnnotatedWith(pipeline.annotationType())) {

                Optional<Model> modelOptional = modelFactory.create(element);
                if (modelOptional.isEmpty()) continue;

                Model model = modelOptional.get();
                pipeline.run(model, context);

            }

        }

    }

}
