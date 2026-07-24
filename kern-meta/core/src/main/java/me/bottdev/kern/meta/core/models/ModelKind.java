package me.bottdev.kern.meta.core.models;

import me.bottdev.kern.meta.core.models.executable.ConstructorModel;
import me.bottdev.kern.meta.core.models.executable.MethodModel;
import me.bottdev.kern.meta.core.models.type.*;
import me.bottdev.kern.meta.core.models.variable.EnumConstantModel;
import me.bottdev.kern.meta.core.models.variable.FieldModel;
import me.bottdev.kern.meta.core.models.variable.ParameterModel;
import me.bottdev.kern.meta.core.models.variable.RecordComponentModel;

public abstract class ModelKind<M extends ElementModel> {

    private final Class<M> modelClass;

    private ModelKind(Class<M> modelClass) {
        this.modelClass = modelClass;
    }

    public M cast(ElementModel model) {
        return modelClass.cast(model);
    }

    public static final ModelKind<ClassModel> CLASS = new ModelKind<>(ClassModel.class) {};
    public static final ModelKind<InterfaceModel> INTERFACE = new ModelKind<>(InterfaceModel.class) {};
    public static final ModelKind<EnumModel> ENUM = new ModelKind<>(EnumModel.class) {};
    public static final ModelKind<RecordModel> RECORD = new ModelKind<>(RecordModel.class) {};
    public static final ModelKind<AnnotationTypeModel> ANNOTATION_TYPE = new ModelKind<>(AnnotationTypeModel.class) {};
    public static final ModelKind<MethodModel> METHOD = new ModelKind<>(MethodModel.class) {};
    public static final ModelKind<ConstructorModel> CONSTRUCTOR = new ModelKind<>(ConstructorModel.class) {};
    public static final ModelKind<FieldModel> FIELD = new ModelKind<>(FieldModel.class) {};
    public static final ModelKind<ParameterModel> PARAMETER = new ModelKind<>(ParameterModel.class) {};
    public static final ModelKind<EnumConstantModel> ENUM_CONSTANT = new ModelKind<>(EnumConstantModel.class) {};
    public static final ModelKind<RecordComponentModel> RECORD_COMPONENT = new ModelKind<>(RecordComponentModel.class) {};
    public static final ModelKind<PackageModel> PACKAGE = new ModelKind<>(PackageModel.class) {};
    public static final ModelKind<TypeParameterModel> TYPE_PARAMETER = new ModelKind<>(TypeParameterModel.class) {};
    public static final ModelKind<AnnotationElementModel> ANNOTATION_ELEMENT = new ModelKind<>(AnnotationElementModel.class) {};

}
