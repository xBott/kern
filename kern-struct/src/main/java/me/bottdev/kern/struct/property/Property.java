package me.bottdev.kern.struct.property;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class Property<T> {

    private final String name;
    private final Class<T> type;

    public static <T> Property<T> of(String name, Class<T> type) {
        return new Property<>(name, type);
    }

    public T cast(Object value) { return type.cast(value); }

}
