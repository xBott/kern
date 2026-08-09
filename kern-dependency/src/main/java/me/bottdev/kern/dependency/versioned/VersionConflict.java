package me.bottdev.kern.dependency.versioned;

import java.util.List;

public record VersionConflict<K>(
        K dependencyKey,
        List<VersionConflictEntry<K>> entries
) {}
