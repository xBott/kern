package me.bottdev.kern.dependency.graph;

import me.bottdev.kern.commons.diagnostic.DiagnosticType;
import me.bottdev.kern.commons.wrapper.DiagnosticResult;
import me.bottdev.kern.dependency.*;
import me.bottdev.kern.dependency.containers.SimpleDependentContainer;
import me.bottdev.kern.struct.algorithms.cycle.SimpleCycleDetector;
import me.bottdev.kern.struct.algorithms.sort.KahnSorter;
import me.bottdev.kern.struct.paths.CyclePath;
import org.junit.jupiter.api.*;

import java.util.List;

import static me.bottdev.kern.dependency.Task.task;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GraphStatefulDependencyResolverTest {

    private StatefulDependencyResolver<String, Task> resolver;

    @BeforeEach
    void setUp() {
        resolver = new GraphStatefulDependencyResolver<>(new KahnSorter(new SimpleCycleDetector()));
    }

    @Test
    @DisplayName("resolveAndRemember: resolves one group of dependencies")
    void resolveAndRemember_single() {

        DependentContainer<String, Task> container = SimpleDependentContainer.<String, Task>builder()
                .add(task("1").build())
                .add(task("2").dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER).build())
                .add(task("3").dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER).build())
                .add(task("4")
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .build())

                .build();

        ResolutionResult<String, Task> result = resolver.resolveAndRemember(container).unwrapOrThrow();
        assertThat(result)
                .satisfies(r -> {
                    List<Task> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(4)
                            .extracting(Task::id)
                            .containsExactly("1", "2", "3", "4");
                })
                .satisfies(r -> {
                    List<List<Task>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(3);
                });


    }

    @Test
    @DisplayName("resolveAndRemember: resolves several groups of dependencies")
    void resolveAndRemember_multiple() {

        DependentContainer<String, Task> container1 = SimpleDependentContainer.<String, Task>builder()
                .add(task("1").build())
                .add(task("2").dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER).build())
                .add(task("3").dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER).build())
                .add(task("4")
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .build())

                .build();

        DependentContainer<String, Task> container2 = SimpleDependentContainer.<String, Task>builder()
                .add(task("5").build())
                .add(task("7").dependsOn("5", DependencyLink.REQUIRED, DependOrder.AFTER).build())
                .add(task("6")
                        .dependsOn("5", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .dependsOn("7", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .build())

                .build();

        DependentContainer<String, Task> container3 = SimpleDependentContainer.<String, Task>builder()
                .add(task("8")
                        .dependsOn("4", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .dependsOn("6", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .build())

                .build();

        ResolutionResult<String, Task> result1 = resolver.resolveAndRemember(container1).unwrapOrThrow();
        ResolutionResult<String, Task> result2 = resolver.resolveAndRemember(container2).unwrapOrThrow();
        ResolutionResult<String, Task> result3 = resolver.resolveAndRemember(container3).unwrapOrThrow();


        assertThat(result1)
                .satisfies(r -> {
                    List<Task> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(4)
                            .extracting(Task::id)
                            .containsExactly("1", "2", "3", "4");
                })
                .satisfies(r -> {
                    List<List<Task>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(3);
                });

        assertThat(result2)
                .satisfies(r -> {
                    List<Task> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(3)
                            .extracting(Task::id)
                            .containsExactly("5", "7", "6");
                })
                .satisfies(r -> {
                    List<List<Task>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(3);
                });


        assertThat(result3)
                .satisfies(r -> {
                    List<Task> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(1)
                            .extracting(Task::id)
                            .containsExactly("8");
                })
                .satisfies(r -> {
                    List<List<Task>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(1);
                });


    }

    @Test
    @DisplayName("resolveAndRemember: duplicate dependency during resolution of several groups")
    void resolveAndRemember_multipleDuplicate() {

        DependentContainer<String, Task> container1 = SimpleDependentContainer.<String, Task>builder()
                .add(task("1").build())
                .add(task("2").dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER).build())
                .add(task("3").dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER).build())
                .add(task("4")
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .build())

                .build();

        DependentContainer<String, Task> container2 = SimpleDependentContainer.<String, Task>builder()
                .add(task("3").build())
                .add(task("1").dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER).build())
                .add(task("6")
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .dependsOn("4", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .build())

                .build();

        ResolutionResult<String, Task> result1 = resolver.resolveAndRemember(container1).unwrapOrThrow();
        DiagnosticResult<ResolutionResult<String, Task>, DependencyDiagnostic> result2 =
                resolver.resolveAndRemember(container2);


        assertThat(result1)
                .satisfies(r -> {
                    List<Task> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(4)
                            .extracting(Task::id)
                            .containsExactly("1", "2", "3", "4");
                })
                .satisfies(r -> {
                    List<List<Task>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(3);
                });


        assertTrue(result2.hasDiagnostics(DiagnosticType.ERROR));
        assertThat(result2.unwrapDiagnostics())
                .contains(DependencyDiagnostic.duplicate("1"))
                .contains(DependencyDiagnostic.duplicate("3"));

    }

    @Test
    @DisplayName("resolveAndRemember: missing dependency during resolution of several groups")
    void resolveAndRemember_multipleMissingDependency() {

        DependentContainer<String, Task> container1 = SimpleDependentContainer.<String, Task>builder()
                .add(task("1").build())
                .add(task("2").dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER).build())
                .add(task("3").dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER).build())
                .add(task("4")
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .build())

                .build();

        DependentContainer<String, Task> container2 = SimpleDependentContainer.<String, Task>builder()
                .add(task("5").build())
                .add(task("7").dependsOn("5", DependencyLink.REQUIRED, DependOrder.AFTER).build())
                .add(task("6")
                        .dependsOn("5", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .dependsOn("7", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .build())

                .build();

        DependentContainer<String, Task> container3 = SimpleDependentContainer.<String, Task>builder()
                .add(task("8")
                        .dependsOn("9", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .dependsOn("13", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .build())

                .build();

        ResolutionResult<String, Task> result1 = resolver.resolveAndRemember(container1).unwrapOrThrow();
        ResolutionResult<String, Task> result2 = resolver.resolveAndRemember(container2).unwrapOrThrow();
        DiagnosticResult<ResolutionResult<String, Task>, DependencyDiagnostic> result3 =
                resolver.resolveAndRemember(container3);


        assertThat(result1)
                .satisfies(r -> {
                    List<Task> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(4)
                            .extracting(Task::id)
                            .containsExactly("1", "2", "3", "4");
                })
                .satisfies(r -> {
                    List<List<Task>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(3);
                });

        assertThat(result2)
                .satisfies(r -> {
                    List<Task> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(3)
                            .extracting(Task::id)
                            .containsExactly("5", "7", "6");
                })
                .satisfies(r -> {
                    List<List<Task>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(3);
                });


        assertTrue(result3.hasDiagnostics(DiagnosticType.ERROR));
        assertThat(result3.unwrapDiagnostics())
                .contains(DependencyDiagnostic.missing("8", "9"))
                .contains(DependencyDiagnostic.missing("8", "13"));

    }

    @Test
    @DisplayName("resolveAndRemember: circular dependency during resolution of several groups")
    void resolveAndRemember_multipleCircular() {

        DependentContainer<String, Task> container1 = SimpleDependentContainer.<String, Task>builder()
                .add(task("1").build())
                .add(task("2").dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER).build())
                .add(task("3").dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER).build())
                .add(task("4")
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .build())

                .build();

        DependentContainer<String, Task> container2 = SimpleDependentContainer.<String, Task>builder()
                .add(task("5").dependsOn("6", DependencyLink.REQUIRED, DependOrder.AFTER).build())
                .add(task("6")
                        .dependsOn("5", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .dependsOn("4", DependencyLink.REQUIRED, DependOrder.AFTER)
                        .build())

                .build();

        ResolutionResult<String, Task> result1 = resolver.resolveAndRemember(container1).unwrapOrThrow();
        DiagnosticResult<ResolutionResult<String, Task>, DependencyDiagnostic> result2 =
                resolver.resolveAndRemember(container2);


        assertThat(result1)
                .satisfies(r -> {
                    List<Task> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(4)
                            .extracting(Task::id)
                            .containsExactly("1", "2", "3", "4");
                })
                .satisfies(r -> {
                    List<List<Task>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(3);
                });

        assertTrue(result2.hasDiagnostics(DiagnosticType.ERROR));
        assertThat(result2.unwrapDiagnostics())
                .contains(DependencyDiagnostic.circular(new CyclePath<>("6", List.of("1", "3", "4", "6", "5"))));

    }

}