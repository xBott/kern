package me.bottdev.kern.meta.core.models.type;

import me.bottdev.kern.meta.core.models.variable.EnumConstantModel;

import java.util.List;

public interface EnumModel extends TypeModel {

    List<EnumConstantModel> constants();

}
