package me.bottdev.kern.meta.apt.models;

import me.bottdev.kern.meta.core.models.ElementHandle;

import javax.lang.model.element.Element;

public record AptElementHandle(Element raw) implements ElementHandle {}
