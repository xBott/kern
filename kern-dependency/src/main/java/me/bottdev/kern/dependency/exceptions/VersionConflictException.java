package me.bottdev.kern.dependency.exceptions;

import lombok.Getter;
import me.bottdev.kern.dependency.versioned.VersionConflict;

import java.util.List;

public class VersionConflictException extends DependencyException {

    @Getter
    private final List<VersionConflict<?>> conflicts;

    public VersionConflictException(List<VersionConflict<?>> conflicts, String message) {
        super(message);
        this.conflicts = conflicts;
    }

}