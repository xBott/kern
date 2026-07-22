package me.bottdev.kern.meta.core.models;

import java.util.List;

public sealed interface AnnotationValueModel permits AnnotationValueModel.OfPrimitive,
        AnnotationValueModel.OfString,
        AnnotationValueModel.OfType,
        AnnotationValueModel.OfEnumConstant,
        AnnotationValueModel.OfAnnotation,
        AnnotationValueModel.OfArray
{

    record OfPrimitive(Object value) implements AnnotationValueModel {}
    record OfString(String value) implements AnnotationValueModel {}
    record OfType(TypeRef value) implements AnnotationValueModel {}
    record OfEnumConstant(TypeRef enumType, String constantName) implements AnnotationValueModel {}
    record OfAnnotation(AnnotationModel value) implements AnnotationValueModel {}
    record OfArray(List<AnnotationValueModel> values) implements AnnotationValueModel {}

}
