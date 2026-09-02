package me.bottdev.kern.meta.apt.models.type;

import me.bottdev.kern.meta.apt.models.ModelUtils;
import me.bottdev.kern.meta.apt.models.TypeRefReader;
import me.bottdev.kern.meta.apt.models.variable.AptRecordComponentModel;
import me.bottdev.kern.meta.core.models.*;
import me.bottdev.kern.meta.core.models.executable.ConstructorModel;
import me.bottdev.kern.meta.core.models.executable.MethodModel;
import me.bottdev.kern.meta.core.models.type.AnnotationTypeModel;
import me.bottdev.kern.meta.core.models.variable.FieldModel;
import me.bottdev.kern.meta.core.models.variable.RecordComponentModel;

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record AptAnnotationTypeModel(
        TypeModelSkeleton skeleton,
        List<AnnotationElementModel> elements
) implements AnnotationTypeModel {

    public static AptAnnotationTypeModel of(TypeElement typeElement) {
        return new AptAnnotationTypeModel(TypeModelSkeleton.read(typeElement), ModelUtils.readAnnotationElements(typeElement));
    }

    @Override public ElementHandle handle() { return skeleton.handle(); }
    @Override public ModelKind<AnnotationTypeModel> kind() { return ModelKind.ANNOTATION_TYPE; }
    @Override public String qualifiedName() { return skeleton.qualifiedName(); }
    @Override public String simpleName() { return skeleton.simpleName(); }
    @Override public String packageName() { return skeleton.packageName(); }
    @Override public Set<Modifier> modifiers() { return skeleton.modifiers(); }
    @Override public List<AnnotationModel> annotations() { return skeleton.annotations(); }
    @Override public Optional<TypeRef> superType() { return skeleton.superType(); }
    @Override public List<TypeRef> interfaces() { return skeleton.interfaces(); }
    @Override public List<TypeParameterModel> typeParameters() { return skeleton.typeParameters(); }
    @Override public List<FieldModel> fields() { return skeleton.fields(); }
    @Override public List<MethodModel> methods() { return skeleton.methods(); }
    @Override public List<ConstructorModel> constructors() { return skeleton.constructors(); }
    @Override public List<TypeRef> nestedTypes() { return skeleton.nestedTypes(); }
    @Override public List<AnnotationElementModel> elements() { return elements; }

}