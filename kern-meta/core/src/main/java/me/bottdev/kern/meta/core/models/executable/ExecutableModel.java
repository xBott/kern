package me.bottdev.kern.meta.core.models.executable;

import me.bottdev.kern.meta.core.models.ModifiedModel;
import me.bottdev.kern.meta.core.models.NamedModel;
import me.bottdev.kern.meta.core.models.TypeParameterModel;
import me.bottdev.kern.meta.core.models.TypeRef;
import me.bottdev.kern.meta.core.models.variable.ParameterModel;

import java.util.List;

public interface ExecutableModel extends NamedModel, ModifiedModel {

    List<ParameterModel> parameters();
    List<TypeParameterModel> typeParameters();
    List<TypeRef> thrownTypes();

}
