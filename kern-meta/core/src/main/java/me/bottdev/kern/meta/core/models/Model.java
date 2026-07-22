package me.bottdev.kern.meta.core.models;

import java.util.List;

public interface Model {

    ModelKind<?> kind();
    List<AnnotationModel> annotations();

}
