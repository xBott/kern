package me.bottdev.kern.meta.core.models;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

public interface Model {

    ModelKind<?> kind();
    List<AnnotationModel> annotations();

    default <A extends Annotation> Optional<A> annotation(Class<A> type) {
        return annotations().stream()
                .filter(a -> a.type().qualifiedName().equals(type.getCanonicalName()))
                .findFirst()
                .map(a -> AnnotationProxyFactory.create(type, a));
    }

}
