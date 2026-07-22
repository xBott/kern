package me.bottdev.kern.meta.apt.models.type;

import me.bottdev.kern.meta.apt.models.ModelUtils;
import me.bottdev.kern.meta.apt.models.TypeRefReader;
import me.bottdev.kern.meta.apt.models.executable.AptExecutableModelReader;
import me.bottdev.kern.meta.apt.models.variable.AptVariableModelReader;
import me.bottdev.kern.meta.core.models.*;
import me.bottdev.kern.meta.core.models.Modifier;
import me.bottdev.kern.meta.core.models.executable.ConstructorModel;
import me.bottdev.kern.meta.core.models.executable.MethodModel;
import me.bottdev.kern.meta.core.models.variable.FieldModel;

import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

record TypeModelSkeleton(
        String qualifiedName,
        String simpleName,
        String packageName,
        Set<Modifier> modifiers,
        List<AnnotationModel> annotations,
        Optional<TypeRef> superType,
        List<TypeRef> interfaces,
        List<FieldModel> fields,
        List<MethodModel> methods,
        List<ConstructorModel> constructors
) {

    static TypeModelSkeleton read(TypeElement type) {
        Optional<TypeRef> superType = type.getSuperclass().getKind() == javax.lang.model.type.TypeKind.NONE
                ? Optional.empty()
                : Optional.of(TypeRefReader.read(type.getSuperclass()));

        List<TypeRef> interfaces = type.getInterfaces().stream().map(TypeRefReader::read).toList();

        List<FieldModel> fields = ElementFilter.fieldsIn(type.getEnclosedElements()).stream()
                .map(AptVariableModelReader::readField)
                .toList();

        List<MethodModel> methods = ElementFilter.methodsIn(type.getEnclosedElements()).stream()
                .map(AptExecutableModelReader::readMethod)
                .toList();

        List<ConstructorModel> constructors = ElementFilter.constructorsIn(type.getEnclosedElements()).stream()
                .map(AptExecutableModelReader::readConstructor)
                .toList();

        return new TypeModelSkeleton(
                type.getQualifiedName().toString(),
                type.getSimpleName().toString(),
                ModelUtils.readPackageName(type),
                ModelUtils.readModifiers(type),
                ModelUtils.readAnnotations(type),
                superType, interfaces, fields, methods, constructors
        );
    }

}