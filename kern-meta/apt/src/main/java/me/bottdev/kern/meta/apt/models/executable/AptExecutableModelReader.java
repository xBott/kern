package me.bottdev.kern.meta.apt.models.executable;

import me.bottdev.kern.meta.apt.models.AptElementHandle;
import me.bottdev.kern.meta.apt.models.ModelUtils;
import me.bottdev.kern.meta.apt.models.TypeRefReader;
import me.bottdev.kern.meta.apt.models.variable.AptVariableModelReader;
import me.bottdev.kern.meta.core.models.TypeRef;
import me.bottdev.kern.meta.core.models.executable.ConstructorModel;
import me.bottdev.kern.meta.core.models.executable.MethodModel;
import me.bottdev.kern.meta.core.models.variable.ParameterModel;

import javax.lang.model.element.ExecutableElement;
import java.util.List;

public final class AptExecutableModelReader {

    public static MethodModel readMethod(ExecutableElement element) {
        return new AptMethodModel(
                new AptElementHandle(element),
                element.getSimpleName().toString(),
                ModelUtils.readModifiers(element),
                ModelUtils.readAnnotations(element),
                readParameters(element),
                ModelUtils.readTypeParameters(element),
                readThrownTypes(element),
                TypeRefReader.read(element.getReturnType()),
                element.isDefault()
        );
    }

    public static ConstructorModel readConstructor(ExecutableElement element) {
        return new AptConstructorModel(
                new AptElementHandle(element),
                element.getSimpleName().toString(),
                ModelUtils.readModifiers(element),
                ModelUtils.readAnnotations(element),
                readParameters(element),
                ModelUtils.readTypeParameters(element),
                readThrownTypes(element)
        );
    }

    private static List<ParameterModel> readParameters(ExecutableElement element) {
        return element.getParameters().stream()
                .map(AptVariableModelReader::readParameter)
                .toList();
    }

    private static List<TypeRef> readThrownTypes(ExecutableElement element) {
        return element.getThrownTypes().stream()
                .map(TypeRefReader::read)
                .toList();
    }

}