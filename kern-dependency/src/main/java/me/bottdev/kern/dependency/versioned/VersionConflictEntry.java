package me.bottdev.kern.dependency.versioned;

import me.bottdev.kern.version.VersionRange;

public record VersionConflictEntry<K>(
        K requesterKey,
        VersionRange range
) {}