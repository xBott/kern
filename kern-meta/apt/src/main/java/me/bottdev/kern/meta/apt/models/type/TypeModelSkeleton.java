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
        List<TypeParameterModel> typeParameters,
        List<FieldModel> fields,
        List<MethodModel> methods,
        List<ConstructorModel> constructors,
        List<TypeRef> nestedTypes
) {

    static TypeModelSkeleton read(TypeElement typeElement) {
        Optional<TypeRef> superType = typeElement.getSuperclass().getKind() == javax.lang.model.type.TypeKind.NONE
                ? Optional.empty()
                : Optional.of(TypeRefReader.read(typeElement.getSuperclass()));

        List<TypeRef> interfaces = typeElement.getInterfaces().stream().map(TypeRefReader::read).toList();

        List<FieldModel> fields = ElementFilter.fieldsIn(typeElement.getEnclosedElements()).stream()
                .map(AptVariableModelReader::readField)
                .toList();

        List<MethodModel> methods = ElementFilter.methodsIn(typeElement.getEnclosedElements()).stream()
                .map(AptExecutableModelReader::readMethod)
                .toList();

        List<ConstructorModel> constructors = ElementFilter.constructorsIn(typeElement.getEnclosedElements()).stream()
                .map(AptExecutableModelReader::readConstructor)
                .toList();

        List<TypeRef> nestedTypes = ElementFilter.typesIn(typeElement.getEnclosedElements()).stream()
                .map(nested -> TypeRef.of(nested.getQualifiedName().toString()))
                .toList();

        return new TypeModelSkeleton(
                typeElement.getQualifiedName().toString(),
                typeElement.getSimpleName().toString(),
                ModelUtils.readPackageName(typeElement),
                ModelUtils.readModifiers(typeElement),
                ModelUtils.readAnnotations(typeElement),
                superType,
                interfaces,
                ModelUtils.readTypeParameters(typeElement),
                fields,
                methods,
                constructors,
                nestedTypes
        );
    }

}