package me.bottdev.kern.meta.apt;

import me.bottdev.kern.meta.apt.configuration.AptProcessorConfigurationBuilder;
import me.bottdev.kern.meta.apt.models.AptModelFactory;
import me.bottdev.kern.meta.core.*;
import me.bottdev.kern.meta.core.configuration.ProcessorConfiguration;
import me.bottdev.kern.meta.core.configuration.ProcessorConfigurationBuilder;
import me.bottdev.kern.meta.core.models.ModelFactory;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public abstract class AbstractMetaProcessor extends AbstractProcessor {

    private ProcessingContext context;
    private ModelFactory<Element> modelFactory;
    private ProcessorConfiguration configuration;

    public ProcessingContext context() {
        return context;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        Logger logger = new AptLogger(processingEnv.getMessager());
        FileFactory fileFactory = new AptFileFactory(processingEnv.getFiler());

        context = new ProcessingContext(logger, fileFactory);
        modelFactory = new AptModelFactory();

        ProcessorConfigurationBuilder configurationBuilder = new AptProcessorConfigurationBuilder();
        configure(configurationBuilder);
        configuration = configurationBuilder.build();

    }

    protected abstract void configure(ProcessorConfigurationBuilder builder);

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        AptProcessingRound round = new AptProcessingRound(modelFactory, roundEnv);
        round.run(configuration, context);
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return configuration.supportedAnnotationTypes();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
