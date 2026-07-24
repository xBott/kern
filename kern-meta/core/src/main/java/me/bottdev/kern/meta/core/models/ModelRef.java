package me.bottdev.kern.meta.core.models;

public interface ModelRef<M extends ElementModel> {

    ModelKind<M> kind();
    TypeRef typeRef();

}
