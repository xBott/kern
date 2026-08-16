package me.bottdev.kern.dependency.graph;

import me.bottdev.kern.commons.wrapper.DiagnosticResult;
import me.bottdev.kern.dependency.*;
import me.bottdev.kern.dependency.containers.SimpleDependentContainer;
import me.bottdev.kern.struct.algorithms.cycle.SimpleCycleDetector;
import me.bottdev.kern.struct.algorithms.sort.KahnSorter;
import me.bottdev.kern.struct.paths.CyclePath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static me.bottdev.kern.dependency.Task.task;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GraphDependencyResolverTest {

    static DependencyResolver resolver;

    @BeforeAll
    static void setAll() {
        resolver = new GraphDependencyResolver(new KahnSorter(new SimpleCycleDetector()));
    }

    @Test
    @DisplayName("resolve: no tasks - nothing to resolve")
    void resolve_empty() {

        DependentContainer<String, Task> container = SimpleDependentContainer.<String, Task>builder().build();
        ResolutionResult<String, Task> result = resolver.resolve(container).unwrapOrThrow();

        assertThat(result)
                .satisfies(r -> {
                    List<Task> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(0);
                })
                .satisfies(r -> {
                    List<List<Task>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(0);
                });

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

        assertThat(result)
                .satisfies(r -> {
                    List<Task> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(1)
                            .extracting(Task::id)
                            .containsExactly("build");
                })
                .satisfies(r -> {
                    List<List<Task>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(1);
                });

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

        assertThat(result)
                .satisfies(r -> {
                    List<Task> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(3)
                            .extracting(Task::id)
                            .containsExactly("build", "test", "publish");
                })
                .satisfies(r -> {
                    List<List<Task>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(3);
                });

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

        assertThat(result)
                .satisfies(r -> {
                    List<Task> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(5)
                            .extracting(Task::id)
                            .containsExactly("build", "test", "copyJar", "publish", "summarize");
                })
                .satisfies(r -> {
                    List<List<Task>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(4);
                });

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

        DiagnosticResult<ResolutionResult<String, Task>, DependencyDiagnostic> result = resolver.resolve(container);
        assertThat(result)
                .isInstanceOf(DiagnosticResult.Failure.class);
        DiagnosticResult.Failure<?, DependencyDiagnostic> failure = (DiagnosticResult.Failure<?, DependencyDiagnostic>) result;
        assertThat(failure.diagnostics())
                .containsExactly(DependencyDiagnostic.missing("test", "build"));
    }

    @Test
    @DisplayName("resolve: should throw circular dependency")
    void resolve_circularDependency() {

        DependentContainer<String, Task> container = SimpleDependentContainer.<String, Task>builder()
                .add(
                        task("a")
                                .dependsOn("c", DependencyLink.REQUIRED, DependOrder.AFTER)
                                .build()
                )
                .add(
                        task("b")
                                .dependsOn("a", DependencyLink.REQUIRED, DependOrder.AFTER)
                                .build()
                )
                .add(
                        task("c")
                                .dependsOn("b", DependencyLink.REQUIRED, DependOrder.AFTER)
                                .build()
                )
                .build();

        DiagnosticResult<ResolutionResult<String, Task>, DependencyDiagnostic> result = resolver.resolve(container);
        assertThat(result)
                .isInstanceOf(DiagnosticResult.Failure.class);
        DiagnosticResult.Failure<?, DependencyDiagnostic> failure = (DiagnosticResult.Failure<?, DependencyDiagnostic>) result;
        assertThat(failure.diagnostics())
                .containsExactly(DependencyDiagnostic.circular(new CyclePath<>(List.of("a", "c", "b", "a"))));

    }

}