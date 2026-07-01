package me.bottdev.kern.commons.key;

public interface TypedKey<T> {

    Class<T> type();

    String qualifier();

    T cast(Object object);

}
