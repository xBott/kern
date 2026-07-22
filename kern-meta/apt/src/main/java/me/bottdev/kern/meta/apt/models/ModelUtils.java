package me.bottdev.kern.meta.apt.models;

import me.bottdev.kern.meta.apt.models.variable.AptAnnotationElementModel;
import me.bottdev.kern.meta.apt.models.variable.AptEnumConstantModel;
import me.bottdev.kern.meta.apt.models.variable.AptRecordComponentModel;
import me.bottdev.kern.meta.core.models.*;
import me.bottdev.kern.meta.core.models.Modifier;
import me.bottdev.kern.meta.core.models.variable.EnumConstantModel;
import me.bottdev.kern.meta.core.models.variable.RecordComponentModel;

import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class ModelUtils {

    public static String readPackageName(TypeElement typeElement) {
        Element enclosing = typeElement.getEnclosingElement();

        while (enclosing != null && enclosing.getKind() != ElementKind.PACKAGE) {
            enclosing = enclosing.getEnclosingElement();
        }
        if (enclosing instanceof PackageElement packageElement) {
            return packageElement.getQualifiedName().toString();
        }
        int lastDot = typeElement.getQualifiedName().toString().lastIndexOf('.');
        return lastDot > 0 ? typeElement.getQualifiedName().toString().substring(0, lastDot) : "";

    }

    public static List<AnnotationModel> readAnnotations(Element element) {
        return element.getAnnotationMirrors().stream()
                .map(AptAnnotationReader::read)
                .collect(Collectors.toList());
    }

    public static Set<Modifier> readModifiers(Element element) {
        return element.getModifiers().stream()
                .map(modifier -> new Modifier(modifier.name()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public static List<TypeParameterModel> readTypeParameters(Parameterizable element) {
        return element.getTypeParameters().stream()
                .map(typeParameter -> (TypeParameterModel) new AptTypeParameterModel(
                        typeParameter.getSimpleName().toString(),
                        ModelUtils.readAnnotations(typeParameter),
                        typeParameter.getBounds().stream().map(TypeRefReader::read).toList()))
                .toList();
    }

    public static List<RecordComponentModel> readRecordComponents(TypeElement recordType) {
        return recordType.getRecordComponents().stream()
                .map(element -> (RecordComponentModel) new AptRecordComponentModel(
                        element.getSimpleName().toString(),
                        ModelUtils.readModifiers(element),
                        ModelUtils.readAnnotations(element),
                        TypeRefReader.read(element.asType())))
                .toList();
    }

    public static List<EnumConstantModel> readEnumConstants(TypeElement enumType) {
        return enumType.getEnclosedElements().stream()
                .filter(element -> element.getKind() == ElementKind.ENUM_CONSTANT)
                .map(VariableElement.class::cast)
                .map(e -> (EnumConstantModel) new AptEnumConstantModel(
                        e.getSimpleName().toString(),
                        ModelUtils.readModifiers(e),
                        ModelUtils.readAnnotations(e),
                        TypeRefReader.read(e.asType())))
                .toList();
    }

    public static List<AnnotationElementModel> readAnnotationElements(TypeElement annotationType) {
        return ElementFilter.methodsIn(annotationType.getEnclosedElements()).stream()
                .map(method -> {
                    AnnotationValue defaultValue = method.getDefaultValue();
                    Optional<AnnotationValueModel> defaultModel = defaultValue == null
                            ? Optional.empty()
                            : Optional.of(AptAnnotationReader.readValue(defaultValue));

                    return (AnnotationElementModel) new AptAnnotationElementModel(
                            method.getSimpleName().toString(),
                            ModelUtils.readAnnotations(method),
                            TypeRefReader.read(method.getReturnType()),
                            defaultModel);
                })
                .toList();
    }

}
