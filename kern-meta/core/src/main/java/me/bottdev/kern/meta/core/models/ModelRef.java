package me.bottdev.kern.meta.core.models;

public interface ModelRef<M extends Model> {

    ModelKind<M> kind();
    TypeRef typeRef();

}
