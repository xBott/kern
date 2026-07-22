package me.bottdev.kern.meta.core.models;

import java.util.Set;

public interface ModifiedModel extends Model {

    Set<Modifier> modifiers();

}
