package me.bottdev.kern.struct.grid;

public record GridPosition<T>(
        int x,
        int y,
        T value
) {}
