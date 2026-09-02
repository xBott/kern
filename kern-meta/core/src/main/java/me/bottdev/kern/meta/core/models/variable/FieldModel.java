package me.bottdev.kern.meta.core.models.variable;

import java.util.Optional;

public interface FieldModel extends VariableModel {

    Optional<Object> constantValue();

}
