package me.bottdev.kern.meta.core.models.executable;

import me.bottdev.kern.meta.core.models.TypeRef;

public interface MethodModel extends ExecutableModel {

    TypeRef returnType();
    boolean isDefault();

}
