package me.bottdev.kern.commons.key;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import lombok.NonNull;

import java.util.Objects;

public record SimpleTypedKey<T>(Class<T> type, String qualifier) implements TypedKey<T> {

    private static final Interner<SimpleTypedKey<?>> INTERNER = Interners.newWeakInterner();

    @SuppressWarnings("unchecked")
    public static <T> SimpleTypedKey<T> of(Class<T> type, String qualifier) {
        return (SimpleTypedKey<T>) INTERNER.intern(new SimpleTypedKey<>(type, qualifier));
    }

    public static <T> SimpleTypedKey<T> of(Class<T> type) {
        return of(type, null);
    }

    public TypedKey<T> qualified(String newQualifier) {
        return of(type, newQualifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypedKey<?> other)) return false;

        return type.equals(other.type())
                && Objects.equals(qualifier, other.qualifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, qualifier);
    }

    @Override
    @NonNull
    public String toString() {
        return qualifier == null
                ? type.getName()
                : type.getName() + "@" + qualifier;
    }

    @Override
    public T cast(Object object) {
        Class<?> objectClass = object.getClass();
        if (objectClass != type) throw new ClassCastException(objectClass + " can't be casted to " + type + " using TypedKey.");
        return type.cast(object);
    }

}
