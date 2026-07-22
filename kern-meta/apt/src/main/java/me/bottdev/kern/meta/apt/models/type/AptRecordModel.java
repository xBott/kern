package me.bottdev.kern.meta.apt.models.type;

import me.bottdev.kern.meta.core.models.*;
import me.bottdev.kern.meta.core.models.executable.ConstructorModel;
import me.bottdev.kern.meta.core.models.executable.MethodModel;
import me.bottdev.kern.meta.core.models.type.RecordModel;
import me.bottdev.kern.meta.core.models.variable.FieldModel;
import me.bottdev.kern.meta.core.models.variable.RecordComponentModel;

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record AptRecordModel(TypeModelSkeleton skeleton) implements RecordModel {

    public static AptRecordModel of(TypeElement type) {
        return new AptRecordModel(TypeModelSkeleton.read(type));
    }

    @Override public ModelKind<RecordModel> kind() { return ModelKind.RECORD; }
    @Override public String qualifiedName() { return skeleton.qualifiedName(); }
    @Override public String simpleName() { return skeleton.simpleName(); }
    @Override public String packageName() { return skeleton.packageName(); }
    @Override public Set<Modifier> modifiers() { return skeleton.modifiers(); }
    @Override public List<AnnotationModel> annotations() { return skeleton.annotations(); }
    @Override public Optional<TypeRef> superType() { return skeleton.superType(); }
    @Override public List<TypeRef> interfaces() { return skeleton.interfaces(); }
    @Override public List<TypeParameterModel> typeParameters() { return List.of(); } // TODO
    @Override public List<FieldModel> fields() { return skeleton.fields(); }
    @Override public List<MethodModel> methods() { return skeleton.methods(); }
    @Override public List<ConstructorModel> constructors() { return skeleton.constructors(); }
    @Override public List<TypeRef> nestedTypes() { return List.of(); } // TODO
    @Override public List<RecordComponentModel> components() { return List.of(); } //TODO
}