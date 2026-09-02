package me.bottdev.kern.meta.apt.models;

import me.bottdev.kern.meta.core.exceptions.DeferException;
import me.bottdev.kern.meta.core.models.TypeRef;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.*;
import java.util.List;

public final class TypeRefReader {

    public static TypeRef read(TypeMirror mirror) {
        return switch (mirror.getKind()) {
            case DECLARED -> readDeclared((DeclaredType) mirror);
            case ARRAY -> readArray((ArrayType) mirror);
            case ERROR -> throw new DeferException(
                    "Type " + mirror + " is not resolved yet. Probably, it was not generated in this round.");
            default -> TypeRef.of(mirror.toString());
        };
    }

    private static TypeRef readDeclared(DeclaredType declared) {
        TypeElement element = (TypeElement) declared.asElement();
        List<TypeRef> typeArgs = declared.getTypeArguments().stream().map(TypeRefReader::read).toList();
        return TypeRef.of(element.getQualifiedName().toString(), typeArgs);
    }

    private static TypeRef readArray(ArrayType array) {
        return TypeRef.of(read(array.getComponentType()).qualifiedName() + "[]");
    }

}