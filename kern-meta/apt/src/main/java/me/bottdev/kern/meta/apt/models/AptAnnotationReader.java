package me.bottdev.kern.meta.apt.models;

import me.bottdev.kern.meta.core.models.AnnotationModel;
import me.bottdev.kern.meta.core.models.AnnotationValueModel;
import me.bottdev.kern.meta.core.models.TypeRef;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor14;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AptAnnotationReader {

    public static AnnotationModel read(AnnotationMirror mirror) {
        TypeElement annotationType = (TypeElement) mirror.getAnnotationType().asElement();

        Map<String, AnnotationValueModel> values = new LinkedHashMap<>();
        mirror.getElementValues().forEach((method, value) ->
                values.put(method.getSimpleName().toString(), value.accept(VALUE_VISITOR, null)));

        return new AptAnnotationModel(
                TypeRef.of(annotationType.getQualifiedName().toString()),
                values
        );
    }

    private static final AnnotationValueVisitor<AnnotationValueModel, Void> VALUE_VISITOR =
            new SimpleAnnotationValueVisitor14<>() {

        @Override
        public AnnotationValueModel visitString(String s, Void unused) {
            return new AnnotationValueModel.OfString(s);
        }

        @Override
        public AnnotationValueModel visitType(TypeMirror t, Void unused) {
            TypeElement typeElement = (TypeElement) ((DeclaredType) t).asElement();
            return new AnnotationValueModel.OfType(TypeRef.of(typeElement.getQualifiedName().toString()));
        }

        @Override
        public AnnotationValueModel visitEnumConstant(VariableElement c, Void unused) {
            TypeElement enumType = (TypeElement) c.getEnclosingElement();
            return new AnnotationValueModel.OfEnumConstant(
                    TypeRef.of(enumType.getQualifiedName().toString()),
                    c.getSimpleName().toString());
        }

        @Override
        public AnnotationValueModel visitAnnotation(AnnotationMirror a, Void unused) {
            return new AnnotationValueModel.OfAnnotation(read(a));
        }

        @Override
        public AnnotationValueModel visitArray(List<? extends AnnotationValue> vals, Void unused) {
            return new AnnotationValueModel.OfArray(vals.stream().map(v -> v.accept(this, null)).toList());
        }

        @Override
        protected AnnotationValueModel defaultAction(Object o, Void unused) {
            return new AnnotationValueModel.OfPrimitive(o);
        }
    };
}