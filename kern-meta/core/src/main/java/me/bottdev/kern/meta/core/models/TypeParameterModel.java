package me.bottdev.kern.meta.core.models;

import java.util.List;

public interface TypeParameterModel extends NamedModel {

    List<TypeRef> bounds();

}
