package me.bottdev.kern.meta.core.configuration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ProcessorConfiguration {

    private final List<Pipeline<?, ?>> definitions;
    private final Set<String> supportedAnnotationTypes;

    public ProcessorConfiguration(List<Pipeline<?, ?>> definitions) {
        this.definitions = definitions;
        this.supportedAnnotationTypes = definitions.stream().map(definition ->
                definition.annotationType().getCanonicalName()
        ).collect(Collectors.toUnmodifiableSet());
    }

    public List<Pipeline<?, ?>> definitions() {
        return definitions;
    }

    public Set<String> supportedAnnotationTypes() {
        return supportedAnnotationTypes;
    }

}
