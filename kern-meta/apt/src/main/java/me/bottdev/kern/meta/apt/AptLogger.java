package me.bottdev.kern.meta.apt;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.meta.core.Logger;
import me.bottdev.kern.meta.core.MessageType;
import me.bottdev.kern.meta.core.models.ElementHandle;
import me.bottdev.kern.meta.core.models.ElementRepresentation;
import me.bottdev.kern.meta.core.models.Model;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.util.EnumMap;
import java.util.Map;

@RequiredArgsConstructor
public class AptLogger implements Logger {

    private final EnumMap<MessageType, Integer> counts = new EnumMap<>(MessageType.class);
    private final Messager messager;

    @Override
    public int count(MessageType type) {
        return counts.getOrDefault(type, 0);
    }

    @Override
    public void message(MessageType type, String message) {
        int prev = count(type);
        counts.put(type, prev + 1);
        switch (type) {
            case INFO -> messager.printMessage(Diagnostic.Kind.NOTE, message);
            case WARN -> messager.printMessage(Diagnostic.Kind.WARNING, message);
            case ERROR -> messager.printMessage(Diagnostic.Kind.ERROR, message);
        }
    }

    @Override
    public void message(MessageType type, String message, Object object) {

        if (object instanceof ElementRepresentation representation) {

            ElementHandle handle = representation.handle();
            Object raw = handle.raw();

            if (raw instanceof Element element) {

                switch (type) {
                    case INFO -> messager.printMessage(Diagnostic.Kind.NOTE, message, element);
                    case WARN -> messager.printMessage(Diagnostic.Kind.WARNING, message, element);
                    case ERROR -> messager.printMessage(Diagnostic.Kind.ERROR, message, element);
                }

            } else {
                message(type, message);

            }

        } else {
            message(type, message);

        }

    }

}
