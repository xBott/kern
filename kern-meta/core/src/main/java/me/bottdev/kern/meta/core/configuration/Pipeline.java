package me.bottdev.kern.meta.core.configuration;

import me.bottdev.kern.meta.core.ProcessingContext;
import me.bottdev.kern.meta.core.models.Model;
import me.bottdev.kern.meta.core.models.ModelKind;

import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;

public record Pipeline<M extends Model, A extends Annotation>(
        ModelKind<M> kind,
        Class<A> annotationType,
        BiConsumer<M, A> peek
) {

    public void run(Model unknownModel, ProcessingContext context) {

        if (unknownModel.kind() != kind) return;

        M model = kind.cast(unknownModel);

        peek.accept(model, null);

    }

}
