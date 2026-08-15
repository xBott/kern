package me.bottdev.kern.dependency;

import me.bottdev.kern.dependency.versioned.VersionedDependencyAware;
import me.bottdev.kern.dependency.versioned.VersionedDependencyRequest;
import me.bottdev.kern.version.SemVersion;
import me.bottdev.kern.version.VersionRange;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Module(
        String id,
        SemVersion version,
        List<VersionedDependencyRequest<String>> dependencies
) implements VersionedDependencyAware<String> {

    public static class Builder {

        private final String id;
        private final SemVersion version;
        private final List<VersionedDependencyRequest<String>> dependencies = new ArrayList<>();

        public Builder(String id, SemVersion version) {
            this.id = id;
            this.version = version;
        }

        public Builder dependsOn(String taskId, DependencyLink link, DependOrder order, VersionRange versionRange) {
            dependencies.add(new VersionedDependencyRequest<>(taskId, link, order, versionRange));
            return this;
        }

        public Module build() {
            return new Module(id, version, dependencies);
        }

    }

    public static Module.Builder module(String id, SemVersion version) {
        return new Module.Builder(id, version);
    }

    @Override
    public @NonNull String dependencyKey() {
        return id;
    }

    @Override
    public @NonNull List<VersionedDependencyRequest<String>> getVersionedDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

}