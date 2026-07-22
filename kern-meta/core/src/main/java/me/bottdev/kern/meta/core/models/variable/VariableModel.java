package me.bottdev.kern.meta.core.models.variable;

import me.bottdev.kern.meta.core.models.ModifiedModel;
import me.bottdev.kern.meta.core.models.NamedModel;
import me.bottdev.kern.meta.core.models.TypeRef;

public interface VariableModel extends NamedModel, ModifiedModel {

    TypeRef type();

}
