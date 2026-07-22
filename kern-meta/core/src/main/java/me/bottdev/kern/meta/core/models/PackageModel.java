package me.bottdev.kern.meta.core.models;

public interface PackageModel extends Model {

    String qualifiedName();
    boolean isUnnamed();

}
