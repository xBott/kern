package me.bottdev.kern.meta.core.models.type;

import me.bottdev.kern.meta.core.models.AnnotationElementModel;

import java.util.List;

public interface AnnotationTypeModel extends TypeModel {

    List<AnnotationElementModel> elements();

}
