package me.bottdev.kern.dependency.exceptions;

import lombok.Getter;
import me.bottdev.kern.version.SemVersion;
import me.bottdev.kern.version.VersionRange;

@Getter
public class VersionMismatchException extends DependencyException {

    private final Object dependent;
    private final Object dependencyKey;
    private final VersionRange requiredRange;
    private final SemVersion actualVersion;

    public <T, K> VersionMismatchException(
            T dependent,
            K dependencyKey,
            VersionRange requiredRange,
            SemVersion actualVersion,
            String message
    ) {
        super(message);
        this.dependent = dependent;
        this.dependencyKey = dependencyKey;
        this.requiredRange = requiredRange;
        this.actualVersion = actualVersion;
    }

}
