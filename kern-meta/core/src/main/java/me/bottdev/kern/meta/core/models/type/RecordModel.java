package me.bottdev.kern.meta.core.models.type;

import me.bottdev.kern.meta.core.models.variable.RecordComponentModel;

import java.util.List;

public interface RecordModel extends TypeModel {

    List<RecordComponentModel> components();

}
