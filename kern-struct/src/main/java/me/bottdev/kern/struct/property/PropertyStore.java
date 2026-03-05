package me.bottdev.kern.struct.property;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public class PropertyStore {

    private final Map<Property<?>, Object> properties = new HashMap<>();

    public Set<Property<?>> keys() {
        return properties.keySet();
    }

    public boolean has(Property<?> property) {
        return properties.containsKey(property);
    }

    public <T> void put(Property<T> property, T value) {
        properties.put(property, value);
    }

    public void putAll(Map<Property<?>, Object> map) {
        properties.putAll(map);
    }

    public <T> T get(Property<T> property) {
        Object value = properties.get(property);
        return property.cast(value);
    }

    public <T> Optional<T> find(Property<T> property) {
        Object value = properties.get(property);
        if (value == null) return Optional.empty();
        return Optional.of(property.cast(value));
    }

    public PropertyStore copy() {
        PropertyStore newStore = new PropertyStore();
        newStore.putAll(properties);
        return newStore;
    }

}
