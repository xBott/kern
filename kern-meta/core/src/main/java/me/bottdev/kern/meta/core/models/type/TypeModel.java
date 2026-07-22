package me.bottdev.kern.meta.core.models.type;

import me.bottdev.kern.meta.core.models.ModifiedModel;
import me.bottdev.kern.meta.core.models.NamedModel;
import me.bottdev.kern.meta.core.models.TypeParameterModel;
import me.bottdev.kern.meta.core.models.TypeRef;
import me.bottdev.kern.meta.core.models.executable.ConstructorModel;
import me.bottdev.kern.meta.core.models.executable.MethodModel;
import me.bottdev.kern.meta.core.models.variable.FieldModel;

import java.util.List;
import java.util.Optional;

public interface TypeModel extends NamedModel, ModifiedModel {

    String qualifiedName();
    String packageName();
    Optional<TypeRef> superType();
    List<TypeRef> interfaces();
    List<TypeParameterModel> typeParameters();
    List<FieldModel> fields();
    List<MethodModel> methods();
    List<ConstructorModel> constructors();
    List<TypeRef> nestedTypes();

}
