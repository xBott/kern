package me.bottdev.kern.meta.core;

public interface Logger {

    void message(MessageType type, String message);

    void message(MessageType type, String message, Object object);

}
