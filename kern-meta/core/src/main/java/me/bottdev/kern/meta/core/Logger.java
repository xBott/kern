package me.bottdev.kern.meta.core;

import me.bottdev.kern.meta.core.models.Model;

public interface Logger {

    void message(MessageType type, String message);

    void message(MessageType type, String message, Model model);

}
