package me.bottdev.kern.meta.core.models;

import me.bottdev.kern.meta.core.models.executable.ConstructorModel;
import me.bottdev.kern.meta.core.models.executable.MethodModel;
import me.bottdev.kern.meta.core.models.type.*;
import me.bottdev.kern.meta.core.models.variable.EnumConstantModel;
import me.bottdev.kern.meta.core.models.variable.FieldModel;
import me.bottdev.kern.meta.core.models.variable.ParameterModel;
import me.bottdev.kern.meta.core.models.variable.RecordComponentModel;

public abstract class ModelKind<M extends Model> {

    private final Class<M> modelClass;

    private ModelKind(Class<M> modelClass) {
        this.modelClass = modelClass;
    }

    public M cast(Model model) {
        return modelClass.cast(model);
    }

    public abstract String keyOf(M model);

    public static final ModelKind<ClassModel> CLASS = new ModelKind<>(ClassModel.class) {
        @Override
        public String keyOf(ClassModel model) {
            return model.qualifiedName();
        }
    };
    public static final ModelKind<InterfaceModel> INTERFACE = new ModelKind<>(InterfaceModel.class) {
        @Override
        public String keyOf(InterfaceModel model) {
            return model.qualifiedName();
        }
    };
    public static final ModelKind<EnumModel> ENUM = new ModelKind<>(EnumModel.class) {
        @Override
        public String keyOf(EnumModel model) {
            return model.qualifiedName();
        }
    };
    public static final ModelKind<RecordModel> RECORD = new ModelKind<>(RecordModel.class) {
        @Override
        public String keyOf(RecordModel model) {
            return model.qualifiedName();
        }
    };
    public static final ModelKind<AnnotationTypeModel> ANNOTATION_TYPE = new ModelKind<>(AnnotationTypeModel.class) {
        @Override
        public String keyOf(AnnotationTypeModel model) {
            return model.qualifiedName();
        }
    };
    public static final ModelKind<MethodModel> METHOD = new ModelKind<>(MethodModel.class) {
        @Override
        public String keyOf(MethodModel model) {
            return model.simpleName();
        }
    };
    public static final ModelKind<ConstructorModel> CONSTRUCTOR = new ModelKind<>(ConstructorModel.class) {
        @Override
        public String keyOf(ConstructorModel model) {
            return model.simpleName();
        }
    };
    public static final ModelKind<FieldModel> FIELD = new ModelKind<>(FieldModel.class) {
        @Override
        public String keyOf(FieldModel model) {
            return model.simpleName();
        }
    };
    public static final ModelKind<ParameterModel> PARAMETER = new ModelKind<>(ParameterModel.class) {
        @Override
        public String keyOf(ParameterModel model) {
            return model.simpleName();
        }
    };
    public static final ModelKind<EnumConstantModel> ENUM_CONSTANT = new ModelKind<>(EnumConstantModel.class) {
        @Override
        public String keyOf(EnumConstantModel model) {
            return model.simpleName();
        }
    };
    public static final ModelKind<RecordComponentModel> RECORD_COMPONENT = new ModelKind<>(RecordComponentModel.class) {
        @Override
        public String keyOf(RecordComponentModel model) {
            return model.simpleName();
        }
    };
    public static final ModelKind<PackageModel> PACKAGE = new ModelKind<>(PackageModel.class) {
        @Override
        public String keyOf(PackageModel model) {
            return model.qualifiedName();
        }
    };
    public static final ModelKind<TypeParameterModel> TYPE_PARAMETER = new ModelKind<>(TypeParameterModel.class) {
        @Override
        public String keyOf(TypeParameterModel model) {
            return model.simpleName();
        }
    };
    public static final ModelKind<AnnotationElementModel> ANNOTATION_ELEMENT = new ModelKind<>(AnnotationElementModel.class) {
        @Override
        public String keyOf(AnnotationElementModel model) {
            return model.simpleName();
        }
    };

}
