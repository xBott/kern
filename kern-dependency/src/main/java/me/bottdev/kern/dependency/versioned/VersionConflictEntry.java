package me.bottdev.kern.dependency.versioned;

import lombok.NonNull;
import me.bottdev.kern.version.VersionRange;

public record VersionConflictEntry<K>(
        @NonNull K requesterKey,
        @NonNull VersionRange range
) {}