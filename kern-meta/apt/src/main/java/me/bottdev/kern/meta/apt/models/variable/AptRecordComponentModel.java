package me.bottdev.kern.meta.apt.models.variable;

import me.bottdev.kern.meta.core.models.AnnotationModel;
import me.bottdev.kern.meta.core.models.ModelKind;
import me.bottdev.kern.meta.core.models.Modifier;
import me.bottdev.kern.meta.core.models.TypeRef;
import me.bottdev.kern.meta.core.models.variable.RecordComponentModel;

import java.util.List;
import java.util.Set;

public record AptRecordComponentModel(
        String simpleName,
        Set<Modifier> modifiers,
        List<AnnotationModel> annotations,
        TypeRef type
) implements RecordComponentModel {

    @Override public ModelKind<RecordComponentModel> kind() { return ModelKind.RECORD_COMPONENT; }

}