package me.bottdev.kern.commons.key;

public final class KeyUtils {

    public static <T> TypedKey<T> key(Class<T> type, String qualifier) {
        return SimpleTypedKey.of(type, qualifier);
    }

    public static <T> TypedKey<T> key(Class<T> type) {
        return key(type, null);
    }

}
