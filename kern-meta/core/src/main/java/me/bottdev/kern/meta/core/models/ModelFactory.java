package me.bottdev.kern.meta.core.models;

import java.util.Optional;

@FunctionalInterface
public interface ModelFactory<E> {

    Optional<ElementModel> create(E element);

}