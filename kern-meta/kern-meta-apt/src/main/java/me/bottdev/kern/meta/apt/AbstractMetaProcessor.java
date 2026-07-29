package me.bottdev.kern.meta.apt;

import me.bottdev.kern.meta.apt.models.AptModelFactory;
import me.bottdev.kern.meta.core.*;
import me.bottdev.kern.meta.core.configuration.ProcessorConfiguration;
import me.bottdev.kern.meta.core.configuration.ProcessorConfigurationBuilder;
import me.bottdev.kern.meta.core.configuration.ProcessorConfigurationBuilderImpl;
import me.bottdev.kern.meta.core.configuration.bound.BoundPipeline;
import me.bottdev.kern.meta.core.configuration.bound.BoundPipelineContext;
import me.bottdev.kern.meta.core.configuration.standalone.StandalonePipeline;
import me.bottdev.kern.meta.core.configuration.standalone.StandalonePipelineContext;
import me.bottdev.kern.meta.core.models.Model;
import me.bottdev.kern.meta.core.models.ModelFactory;
import me.bottdev.kern.meta.core.models.ModelRegistry;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractMetaProcessor extends AbstractProcessor {

    private Filer filer;
    private ProcessingContext context;
    private ModelFactory<Element> modelFactory;
    private ProcessorConfiguration configuration;

    public ProcessingContext context() {
        return context;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.filer = processingEnv.getFiler();

        Logger logger = new AptLogger(processingEnv.getMessager());
        FileFactory fileFactory = new AptFileFactory(filer);
        ModelRegistry modelRegistry = new ModelRegistry();

        context = new ProcessingContext(logger, fileFactory, modelRegistry);
        modelFactory = new AptModelFactory();

        ProcessorConfigurationBuilder configurationBuilder = new ProcessorConfigurationBuilderImpl(context);
        configure(configurationBuilder);
        configuration = configurationBuilder.build();

    }

    protected abstract void configure(ProcessorConfigurationBuilder builder);

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (roundEnv.processingOver()) {
            if (context.logger().count(MessageType.ERROR) > 0) return false;
            runAfterAllPipelines();
            return false;

        } else {

            boolean generated = false;
            generated |= runBoundPipelines(roundEnv);
            generated |= runAfterRoundPipelines();
            return generated;

        }

    }

    private void runAfterAllPipelines() {
        StandalonePipelineContext pipelineContext = new StandalonePipelineContext(context);
        configuration.afterAllPipelines().forEach(pipeline -> pipeline.run(pipelineContext));
    }

    private boolean runAfterRoundPipelines() {
        StandalonePipelineContext pipelineContext = new StandalonePipelineContext(context);

        boolean generated = false;

        for (StandalonePipeline pipeline : configuration.afterRoundPipelines()) {
            generated |= pipeline.run(pipelineContext);
        }

        return generated;
    }

    private boolean runBoundPipelines(RoundEnvironment roundEnv) {

        boolean generated = false;

        for (BoundPipeline<?> pipeline : configuration.boundPipelines()) {

            for (Element element : roundEnv.getElementsAnnotatedWith(pipeline.annotationType())) {

                Optional<Model> modelOptional = modelFactory.create(element);
                if (modelOptional.isEmpty()) continue;
                Model rawModel = modelOptional.get();
                Model indexed = context.modelRegistry().register(pipeline.kind(), rawModel);
                generated |= pipeline.run(new BoundPipelineContext(indexed, context));

            }

        }

        return generated;

    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return configuration.supportedAnnotationTypes();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    protected Filer filer() {
        return filer;
    }

}
