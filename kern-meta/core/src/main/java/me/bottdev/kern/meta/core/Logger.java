package me.bottdev.kern.meta.core;

public interface Logger {

    int count(MessageType type);

    void message(MessageType type, String message);

    void message(MessageType type, String message, Object object);

}
