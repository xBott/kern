package me.bottdev.kern.meta.apt.models.variable;

import me.bottdev.kern.meta.core.models.*;
import me.bottdev.kern.meta.core.models.variable.RecordComponentModel;

import java.util.List;
import java.util.Set;

public record AptRecordComponentModel(
        ElementHandle handle,
        String simpleName,
        Set<Modifier> modifiers,
        List<AnnotationModel> annotations,
        TypeRef type
) implements RecordComponentModel {

    @Override public ModelKind<RecordComponentModel> kind() { return ModelKind.RECORD_COMPONENT; }

}