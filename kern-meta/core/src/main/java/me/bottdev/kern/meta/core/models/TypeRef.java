package me.bottdev.kern.meta.core.models;

import java.util.List;

public sealed interface TypeRef permits TypeRef.Simple {

    String qualifiedName();
    List<TypeRef> typeArguments();

    record Simple(
            String qualifiedName,
            List<TypeRef> typeArguments
    ) implements TypeRef {}

    static TypeRef of(String qualifiedName) {
        return of(qualifiedName, List.of());
    }

    static TypeRef of(String qualifiedName, List<TypeRef> typeArguments) {
        return new Simple(qualifiedName, typeArguments);
    }

}
