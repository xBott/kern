package me.bottdev.kern.meta.core.models;

public interface PackageModel extends ElementModel {

    String qualifiedName();
    boolean isUnnamed();

}
