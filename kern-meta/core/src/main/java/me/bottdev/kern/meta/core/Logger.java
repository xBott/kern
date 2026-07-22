package me.bottdev.kern.meta.core;

import javax.lang.model.element.Element;

public interface Logger {

    void info(String message);
    void warn(String message, Element element);
    void error(String message, Element element);

}
