package me.bottdev.kern.meta.core.models;

public sealed interface ModelRef<M extends Model> permits ModelRef.Simple {

    ModelKind<M> kind();
    TypeRef type();

    record Simple<M extends Model>(
            ModelKind<M> kind,
            TypeRef type
    ) implements ModelRef<M> {}

    static <M extends Model> ModelRef<M> of(ModelKind<M> kind, TypeRef type) {
        return new Simple<>(kind, type);
    }

}
