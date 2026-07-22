package me.bottdev.kern.meta.apt.models.variable;

import me.bottdev.kern.meta.apt.models.ModelUtils;
import me.bottdev.kern.meta.apt.models.TypeRefReader;
import me.bottdev.kern.meta.core.models.variable.FieldModel;
import me.bottdev.kern.meta.core.models.variable.ParameterModel;

import javax.lang.model.element.VariableElement;
import java.util.Optional;

public final class AptVariableModelReader {

    public static FieldModel readField(VariableElement element) {
        return new AptFieldModel(
                element.getSimpleName().toString(),
                ModelUtils.readModifiers(element),
                ModelUtils.readAnnotations(element),
                TypeRefReader.read(element.asType()),
                Optional.ofNullable(element.getConstantValue())
        );
    }

    public static ParameterModel readParameter(VariableElement element) {
        return new AptParameterModel(
                element.getSimpleName().toString(),
                ModelUtils.readModifiers(element),
                ModelUtils.readAnnotations(element),
                TypeRefReader.read(element.asType())
        );
    }

}