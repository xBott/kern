package me.bottdev.kern.meta.apt;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.meta.core.Logger;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

@RequiredArgsConstructor
public class AptLogger implements Logger {

    private final Messager messager;

    @Override
    public void info(String message) {
        messager.printMessage(Diagnostic.Kind.NOTE, message);
    }

    @Override
    public void warn(String message, Element element) {
        messager.printMessage(Diagnostic.Kind.WARNING, message, element);
    }

    @Override
    public void error(String message, Element element) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }

}
