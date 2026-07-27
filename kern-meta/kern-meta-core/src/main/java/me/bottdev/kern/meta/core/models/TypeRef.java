package me.bottdev.kern.meta.core.models;

import java.util.List;

public sealed interface TypeRef permits TypeRef.Simple {

    String qualifiedName();
    List<TypeRef> typeArguments();

    <M extends Model> ModelRef<M> as(ModelKind<M> kind);

    record Simple(
            String qualifiedName,
            List<TypeRef> typeArguments
    ) implements TypeRef {

        @Override
        public <M extends Model> ModelRef<M> as(ModelKind<M> kind) {
            return ModelRef.of(kind, this);
        }

    }

    static TypeRef of(String qualifiedName) {
        return of(qualifiedName, List.of());
    }

    static TypeRef of(String qualifiedName, List<TypeRef> typeArguments) {
        return new Simple(qualifiedName, typeArguments);
    }

}
