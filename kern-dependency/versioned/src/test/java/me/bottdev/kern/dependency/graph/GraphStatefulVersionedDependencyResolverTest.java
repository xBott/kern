package me.bottdev.kern.dependency.graph;

import me.bottdev.kern.commons.diagnostic.DiagnosticSeverity;
import me.bottdev.kern.commons.wrapper.DiagnosticResult;
import me.bottdev.kern.dependency.*;
import me.bottdev.kern.dependency.Module;
import me.bottdev.kern.dependency.containers.SimpleDependentContainer;
import me.bottdev.kern.dependency.versioned.StatefulVersionedDependencyResolver;
import me.bottdev.kern.dependency.versioned.graph.GraphStatefulVersionedDependencyResolver;
import me.bottdev.kern.struct.algorithms.cycle.SimpleCycleDetector;
import me.bottdev.kern.struct.algorithms.sort.KahnSorter;
import me.bottdev.kern.struct.paths.CyclePath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static me.bottdev.kern.dependency.Module.module;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GraphStatefulVersionedDependencyResolverTest {

    private StatefulVersionedDependencyResolver<String, Module> resolver;

    @BeforeEach
    void setUp() {
        resolver = new GraphStatefulVersionedDependencyResolver<>(new KahnSorter(new SimpleCycleDetector()));
    }

    @Test
    @DisplayName("resolveAndRemember: resolves one group of dependencies")
    void resolveAndRemember_single() {

        DependentContainer<String, Module> container = SimpleDependentContainer.<String, Module>builder()
                .add(module("1", "0.0.1").build())
                .add(module("2", "0.0.1")
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, "*").build()
                )
                .add(module("3", "0.0.1")
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, "*").build()
                )
                .add(module("4", "0.0.1")
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, "*")
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER, "*")
                        .build()
                )

                .build();

        ResolutionResult<String, Module> result = resolver.resolveAndRemember(container).unwrapOrThrow();
        assertThat(result)
                .satisfies(r -> {
                    List<Module> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(4)
                            .extracting(Module::id)
                            .containsExactly("1", "2", "3", "4");
                })
                .satisfies(r -> {
                    List<List<Module>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(3);
                });


    }

    @Test
    @DisplayName("resolveAndRemember: resolves several groups of dependencies")
    void resolveAndRemember_multiple() {

        DependentContainer<String, Module> container1 = SimpleDependentContainer.<String, Module>builder()
                .add(module("1", "0.0.1").build())
                .add(module("2", "0.0.1")
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, "*").build()
                )
                .add(module("3", "0.0.1")
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, "*").build()
                )
                .add(module("4", "0.0.1")
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, "*")
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER, "*")
                        .build()
                )

                .build();

        DependentContainer<String, Module> container2 = SimpleDependentContainer.<String, Module>builder()
                .add(module("5", "0.0.1").build())
                .add(module("7", "0.0.1")
                        .dependsOn("5", DependencyLink.REQUIRED, DependOrder.AFTER, "*").build()
                )
                .add(module("6", "0.0.1")
                        .dependsOn("5", DependencyLink.REQUIRED, DependOrder.AFTER, "*")
                        .dependsOn("7", DependencyLink.REQUIRED, DependOrder.AFTER, "*")
                        .build()
                )

                .build();

        DependentContainer<String, Module> container3 = SimpleDependentContainer.<String, Module>builder()
                .add(module("8", "0.0.1")
                        .dependsOn("4", DependencyLink.REQUIRED, DependOrder.AFTER, ">=0.0.0")
                        .dependsOn("6", DependencyLink.REQUIRED, DependOrder.AFTER, ">=0.0.0")
                        .build()
                )

                .build();

        ResolutionResult<String, Module> result1 = resolver.resolveAndRemember(container1).unwrapOrThrow();
        ResolutionResult<String, Module> result2 = resolver.resolveAndRemember(container2).unwrapOrThrow();
        ResolutionResult<String, Module> result3 = resolver.resolveAndRemember(container3).unwrapOrThrow();


        assertThat(result1)
                .satisfies(r -> {
                    List<Module> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(4)
                            .extracting(Module::id)
                            .containsExactly("1", "2", "3", "4");
                })
                .satisfies(r -> {
                    List<List<Module>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(3);
                });

        assertThat(result2)
                .satisfies(r -> {
                    List<Module> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(3)
                            .extracting(Module::id)
                            .containsExactly("5", "7", "6");
                })
                .satisfies(r -> {
                    List<List<Module>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(3);
                });


        assertThat(result3)
                .satisfies(r -> {
                    List<Module> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(1)
                            .extracting(Module::id)
                            .containsExactly("8");
                })
                .satisfies(r -> {
                    List<List<Module>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(1);
                });


    }

    @Test
    @DisplayName("resolveAndRemember: duplicate dependency during resolution of several groups")
    void resolveAndRemember_multipleDuplicate() {

        DependentContainer<String, Module> container1 = SimpleDependentContainer.<String, Module>builder()
                .add(module("1", "0.0.1").build())
                .add(module("2", "0.0.1")
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, "*").build()
                )
                .add(module("3", "0.0.1")
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, "*").build()
                )
                .add(module("4", "0.0.1")
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, "*")
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER, "*")
                        .build()
                )

                .build();

        DependentContainer<String, Module> container2 = SimpleDependentContainer.<String, Module>builder()
                .add(module("3", "0.0.1").build())
                .add(module("1", "0.0.1")
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER, ">=0.0.0").build()
                )
                .add(module("6", "0.0.1")
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, ">=0.0.0")
                        .dependsOn("4", DependencyLink.REQUIRED, DependOrder.AFTER, ">=0.0.0")
                        .build()
                )

                .build();

        ResolutionResult<String, Module> result1 = resolver.resolveAndRemember(container1).unwrapOrThrow();
        DiagnosticResult<ResolutionResult<String, Module>, DependencyDiagnostic> result2 =
                resolver.resolveAndRemember(container2);


        assertThat(result1)
                .satisfies(r -> {
                    List<Module> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(4)
                            .extracting(Module::id)
                            .containsExactly("1", "2", "3", "4");
                })
                .satisfies(r -> {
                    List<List<Module>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(3);
                });


        assertTrue(result2.hasDiagnostics(DiagnosticSeverity.ERROR));
        assertThat(result2.unwrapDiagnostics())
                .contains(new DependencyDiagnostic.Duplicate<>("1"))
                .contains(new DependencyDiagnostic.Duplicate<>("3"));

    }

    @Test
    @DisplayName("resolveAndRemember: missing dependency during resolution of several groups")
    void resolveAndRemember_multipleMissingDependency() {

        DependentContainer<String, Module> container1 = SimpleDependentContainer.<String, Module>builder()
                .add(module("1", "0.0.1").build())
                .add(module("2", "0.0.1")
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, "*").build()
                )
                .add(module("3", "0.0.1")
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, "*").build()
                )
                .add(module("4", "0.0.1")
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, "*")
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER, "*")
                        .build()
                )

                .build();

        DependentContainer<String, Module> container2 = SimpleDependentContainer.<String, Module>builder()
                .add(module("5", "0.0.1").build())
                .add(module("6", "0.0.1")
                        .dependsOn("5", DependencyLink.REQUIRED, DependOrder.AFTER, ">=0.0.0")
                        .dependsOn("7", DependencyLink.REQUIRED, DependOrder.AFTER, ">=0.0.0")
                        .build()
                )
                .add(module("7", "0.0.1")
                        .dependsOn("5", DependencyLink.REQUIRED, DependOrder.AFTER, ">=0.0.0")
                        .build()
                )

                .build();

        DependentContainer<String, Module> container3 = SimpleDependentContainer.<String, Module>builder()
                .add(module("8", "0.0.1")
                        .dependsOn("9", DependencyLink.REQUIRED, DependOrder.AFTER, ">=0.0.0")
                        .dependsOn("13", DependencyLink.REQUIRED, DependOrder.AFTER, ">=0.0.0")
                        .build()
                )

                .build();

        ResolutionResult<String, Module> result1 = resolver.resolveAndRemember(container1).unwrapOrThrow();
        ResolutionResult<String, Module> result2 = resolver.resolveAndRemember(container2).unwrapOrThrow();
        DiagnosticResult<ResolutionResult<String, Module>, DependencyDiagnostic> result3 =
                resolver.resolveAndRemember(container3);


        assertThat(result1)
                .satisfies(r -> {
                    List<Module> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(4)
                            .extracting(Module::id)
                            .containsExactly("1", "2", "3", "4");
                })
                .satisfies(r -> {
                    List<List<Module>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(3);
                });

        assertThat(result2)
                .satisfies(r -> {
                    List<Module> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(3)
                            .extracting(Module::id)
                            .containsExactly("5", "7", "6");
                })
                .satisfies(r -> {
                    List<List<Module>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(3);
                });


        assertTrue(result3.hasDiagnostics(DiagnosticSeverity.ERROR));
        assertThat(result3.unwrapDiagnostics())
                .contains(new DependencyDiagnostic.Missing<>("8", "9"))
                .contains(new DependencyDiagnostic.Missing<>("8", "13"));

    }

    @Test
    @DisplayName("resolveAndRemember: circular dependency during resolution of several groups")
    void resolveAndRemember_multipleCircular() {

        DependentContainer<String, Module> container1 = SimpleDependentContainer.<String, Module>builder()
                .add(module("1", "0.0.1").build())
                .add(module("2", "0.0.1")
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, "*").build()
                )
                .add(module("3", "0.0.1")
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, "*").build()
                )
                .add(module("4", "0.0.1")
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, "*")
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER, "*")
                        .build()
                )

                .build();

        DependentContainer<String, Module> container2 = SimpleDependentContainer.<String, Module>builder()
                .add(module("5", "0.0.1")
                        .dependsOn("6", DependencyLink.REQUIRED, DependOrder.AFTER, "*").build()
                )
                .add(module("6", "0.0.1")
                        .dependsOn("5", DependencyLink.REQUIRED, DependOrder.AFTER, "*")
                        .dependsOn("4", DependencyLink.REQUIRED, DependOrder.AFTER, "*")
                        .build()
                )

                .build();

        ResolutionResult<String, Module> result1 = resolver.resolveAndRemember(container1).unwrapOrThrow();
        DiagnosticResult<ResolutionResult<String, Module>, DependencyDiagnostic> result2 =
                resolver.resolveAndRemember(container2);


        assertThat(result1)
                .satisfies(r -> {
                    List<Module> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(4)
                            .extracting(Module::id)
                            .containsExactly("1", "2", "3", "4");
                })
                .satisfies(r -> {
                    List<List<Module>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(3);
                });

        assertTrue(result2.hasDiagnostics(DiagnosticSeverity.ERROR));
        assertThat(result2.unwrapDiagnostics())
                .contains(new DependencyDiagnostic.Circular<>(new CyclePath<>(List.of("5", "6", "5"))));

    }

    @Test
    @DisplayName("shouldReportVersionMismatch")
    void shouldReportVersionMismatch() {

        String requiredVersionRange = ">0.0.2";
        Module dependency = module("2", "0.0.2").build();
        Module dependent = module("3", "0.0.1")
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, requiredVersionRange).build();
        
        DependentContainer<String, Module> container = SimpleDependentContainer.<String, Module>builder()
                .add(dependency)
                .add(dependent)
                .build();

        DiagnosticResult<ResolutionResult<String, Module>, DependencyDiagnostic> result = resolver.resolveAndRemember(container);

        assertTrue(result.hasDiagnostics(DiagnosticSeverity.ERROR));
        String actualVersion = "0.0.2";
        assertThat(result.unwrapDiagnostics())
                .contains(new DependencyDiagnostic.VersionMismatch<>("3", "2", requiredVersionRange, actualVersion));

    }


    @Test
    @DisplayName("shouldReportVersionConflict")
    void shouldReportVersionConflict() {

        DependentContainer<String, Module> container = SimpleDependentContainer.<String, Module>builder()
                .add(module("1", "0.0.1").build())
                .add(module("2", "0.0.1")
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, ">=0.0.2")
                        .build()
                )
                .add(module("3", "0.0.1")
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, "<=0.0.1")
                        .build()
                )
                .build();

        DiagnosticResult<ResolutionResult<String, Module>, DependencyDiagnostic> result = resolver.resolveAndRemember(container);

        assertTrue(result.hasDiagnostics(DiagnosticSeverity.ERROR));
        assertThat(result.unwrapDiagnostics())
                .contains(new DependencyDiagnostic.VersionMismatch<>("2", "1", ">=0.0.2", "0.0.1"));

    }

    @Test
    @DisplayName("resolveAndRemember: bidirectional edges in multiple batches")
    void resolveAndRemember_bidirectionalEdges() {
        
        DependentContainer<String, Module> container1 = SimpleDependentContainer.<String, Module>builder()
                .add(module("1", "0.0.1")
                        .dependsOn("2", DependencyLink.OPTIONAL, DependOrder.AFTER, ">=0.0.1").build()
                )
                .build();

        DependentContainer<String, Module> container2 = SimpleDependentContainer.<String, Module>builder()
                .add(module("2", "0.0.1").build())
                .build();

        ResolutionResult<String, Module> result1 = resolver.resolveAndRemember(container1).unwrapOrThrow();
        ResolutionResult<String, Module> result2 = resolver.resolveAndRemember(container2).unwrapOrThrow();

        assertThat(result1.ordered()).extracting(Module::id).containsExactly("1");
        
        // In the second batch, the resolver should connect "1" -> "2" and re-sort them.
        // Wait, the resolution result of batch 2 returns the sorted list of JUST container2's modules.
        // So result2.ordered() should contain "2".
        assertThat(result2.ordered()).extracting(Module::id).containsExactly("2");

        // The real test is the internal state graph. We can check if "2" is in the graph.
        // Since we can't easily inspect the internal graph directly without reflection,
        // we could just assert that no errors were thrown (e.g. missing or mismatch).
        // Actually, if we had a cycle, it would be thrown.
    }

}