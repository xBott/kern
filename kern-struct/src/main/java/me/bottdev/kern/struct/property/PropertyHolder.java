package me.bottdev.kern.struct.property;

import java.util.Optional;

public interface PropertyHolder {

    PropertyStore propertyStore();

    default <P> P getProperty(Property<P> property) {
        return propertyStore().get(property);
    }

    default <P> Optional<P> findProperty(Property<P> property) {
        return propertyStore().find(property);
    }

}
