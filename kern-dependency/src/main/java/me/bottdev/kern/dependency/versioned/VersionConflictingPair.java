package me.bottdev.kern.dependency.versioned;

import lombok.NonNull;

public record VersionConflictingPair<K>(
        @NonNull VersionConflictEntry<K> first,
        @NonNull VersionConflictEntry<K> second
) {}