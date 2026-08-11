package me.bottdev.kern.dependency.graph;

import me.bottdev.kern.commons.wrapper.DiagnosticResult;
import me.bottdev.kern.dependency.*;
import me.bottdev.kern.dependency.containers.SimpleDependentContainer;
import me.bottdev.kern.struct.algorithms.cycle.CycleDetector;
import me.bottdev.kern.struct.algorithms.cycle.SimpleCycleDetector;
import me.bottdev.kern.struct.algorithms.sort.KahnSorter;
import me.bottdev.kern.struct.algorithms.sort.TopologicalSorter;
import me.bottdev.kern.struct.paths.CyclePath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GraphDependencyResolverTest {

    record Task(String id, List<SimpleDependencyRequest<String>> dependencies) implements DependencyAware<String> {

        static class Builder {

            private final String id;
            private final List<SimpleDependencyRequest<String>> dependencies = new ArrayList<>();

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

        @Override
        public String dependencyKey() {
            return id;
        }

        @Override
        public List<DependencyRequest<String>> getDependencies() {
            return Collections.unmodifiableList(dependencies);
        }

    }

    static CycleDetector cycleDetector;
    static TopologicalSorter sorter;
    static DependencyResolver resolver;

    @BeforeAll
    static void setAll() {
        cycleDetector = new SimpleCycleDetector();
        sorter = new KahnSorter(cycleDetector);
        resolver = new GraphDependencyResolver(sorter);
    }

    Task.Builder task(String id) {
        return new Task.Builder(id);
    }

    @Test
    @DisplayName("resolve: no tasks - nothing to resolve")
    void resolve_empty() {

        DependentContainer<String, Task> container = SimpleDependentContainer.<String, Task>builder().build();
        ResolutionResult<String, Task> result = resolver.resolve(container).unwrapOrThrow();

        assertEquals(0, result.ordered().size(), "Ordered size is incorrect");
        assertEquals(0, result.layers().size(), "Layers size is incorrect");

    }

    @Test
    @DisplayName("resolve: single task - nothing to resolve")
    void resolve_single() {

        DependentContainer<String, Task> container = SimpleDependentContainer.<String, Task>builder()
                .add(
                        task("build").build()
                )
                .build();

        ResolutionResult<String, Task> result = resolver.resolve(container).unwrapOrThrow();

        assertEquals(1, result.ordered().size(), "Ordered size is incorrect");
        assertEquals(1, result.layers().size(), "Layers size is incorrect");

    }

    @Test
    @DisplayName("resolve: multiple tasks depending on each other")
    void resolve_linear() {

        DependentContainer<String, Task> container = SimpleDependentContainer.<String, Task>builder()
                .add(
                        task("build").build()
                )
                .add(
                        task("test")
                                .dependsOn("build", DependencyLink.REQUIRED, DependOrder.AFTER)
                                .build()
                )
                .add(
                        task("publish")
                                .dependsOn("test", DependencyLink.REQUIRED, DependOrder.AFTER)
                                .build()
                )
                .build();

        ResolutionResult<String, Task> result = resolver.resolve(container).unwrapOrThrow();

        assertEquals(3, result.ordered().size(), "Ordered size is incorrect");
        assertEquals(3, result.layers().size(), "Layers size is incorrect");

    }

    @Test
    @DisplayName("resolve: multiple tasks depending on each other with branching")
    void resolve_branch() {

        DependentContainer<String, Task> container = SimpleDependentContainer.<String, Task>builder()
                .add(
                        task("build").build()
                )
                .add(
                        task("test")
                                .dependsOn("build", DependencyLink.REQUIRED, DependOrder.AFTER)
                                .build()
                )
                .add(
                        task("copyJar")
                                .dependsOn("test", DependencyLink.REQUIRED, DependOrder.AFTER)
                                .build()
                )
                .add(
                        task("publish")
                                .dependsOn("test", DependencyLink.REQUIRED, DependOrder.AFTER)
                                .build()
                ).add(
                        task("summarize")
                                .dependsOn("publish", DependencyLink.REQUIRED, DependOrder.AFTER)
                                .dependsOn("copyJar", DependencyLink.REQUIRED, DependOrder.AFTER)
                                .build()
                )
                .build();

        ResolutionResult<String, Task> result = resolver.resolve(container).unwrapOrThrow();

        assertEquals(5, result.ordered().size(), "Ordered size is incorrect");
        assertEquals(4, result.layers().size(), "Layers size is incorrect");

    }

    @Test
    @DisplayName("resolve: should throw if dependency is missing")
    void resolve_missingDependency() {

        DependentContainer<String, Task> container = SimpleDependentContainer.<String, Task>builder()
                .add(
                        task("test")
                                .dependsOn("build", DependencyLink.REQUIRED, DependOrder.AFTER)
                                .build()
                )
                .build();

        DiagnosticResult<ResolutionResult<String, Task>> result = resolver.resolve(container);
        assertThat(result)
                .isInstanceOf(DiagnosticResult.Failure.class);
        DiagnosticResult.Failure<?> failure = (DiagnosticResult.Failure<?>) result;
        assertThat(failure.diagnostics())
                .containsExactly(DependencyDiagnostic.missing("test", "build"));
    }

    @Test
    @DisplayName("resolve: should throw circular dependency")
    void resolve_circularDependency() {

        DependentContainer<String, Task> container = SimpleDependentContainer.<String, Task>builder()
                .add(
                        task("a")
                                .dependsOn("b", DependencyLink.REQUIRED, DependOrder.AFTER)
                                .build()
                )
                .add(
                        task("b")
                                .dependsOn("c", DependencyLink.REQUIRED, DependOrder.AFTER)
                                .build()
                )
                .add(
                        task("c")
                                .dependsOn("a", DependencyLink.REQUIRED, DependOrder.AFTER)
                                .build()
                )
                .build();

        DiagnosticResult<ResolutionResult<String, Task>> result = resolver.resolve(container);
        assertThat(result)
                .isInstanceOf(DiagnosticResult.Failure.class);
        DiagnosticResult.Failure<?> failure = (DiagnosticResult.Failure<?>) result;
        assertThat(failure.diagnostics())
                .containsExactly(DependencyDiagnostic.circular(new CyclePath<>("a", List.of("a", "b", "c"))));

    }

}