package me.bottdev.kern.dependency;

import lombok.NonNull;
import me.bottdev.kern.dependency.simple.SimpleDependencyRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Task(String id, List<DependencyRequest<String>> dependencies) implements DependencyAware<String> {

    public static class Builder {

        private final String id;
        private final List<DependencyRequest<String>> dependencies = new ArrayList<>();

        public Builder(String id) {
            this.id = id;
        }

        public Builder dependsOn(String taskId, DependencyLink link, DependOrder order) {
            dependencies.add(new SimpleDependencyRequest<>(taskId, link, order));
            return this;
        }

        public Task build() {
            return new Task(id, dependencies);
        }

    }

    public static Task.Builder task(String id) {
        return new Task.Builder(id);
    }

    @Override
    public @NonNull String dependencyKey() {
        return id;
    }

    @Override
    public @NonNull List<DependencyRequest<String>> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

}