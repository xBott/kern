package me.bottdev.kern.dependency.versioned;

public record VersionConflictingPair<K>(
        VersionConflictEntry<K> first,
        VersionConflictEntry<K> second
) {}