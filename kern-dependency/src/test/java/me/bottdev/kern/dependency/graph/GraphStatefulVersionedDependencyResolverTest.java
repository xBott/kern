package me.bottdev.kern.dependency.graph;

import me.bottdev.kern.commons.diagnostic.DiagnosticType;
import me.bottdev.kern.commons.wrapper.DiagnosticResult;
import me.bottdev.kern.dependency.*;
import me.bottdev.kern.dependency.Module;
import me.bottdev.kern.dependency.containers.SimpleDependentContainer;
import me.bottdev.kern.dependency.versioned.VersionConflictEntry;
import me.bottdev.kern.struct.algorithms.cycle.SimpleCycleDetector;
import me.bottdev.kern.struct.algorithms.sort.KahnSorter;
import me.bottdev.kern.struct.paths.CyclePath;
import me.bottdev.kern.version.SemVersion;
import me.bottdev.kern.version.SemVersionParser;
import me.bottdev.kern.version.VersionRange;
import me.bottdev.kern.version.VersionRangeParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static me.bottdev.kern.dependency.Module.module;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GraphStatefulVersionedDependencyResolverTest {

    private StatefulDependencyResolver<String, Module> resolver;

    @BeforeEach
    void setUp() {
        resolver = new GraphStatefulVersionedDependencyResolver<>(new KahnSorter(new SimpleCycleDetector()));
    }

    @Test
    @DisplayName("resolveAndRemember: resolves one group of dependencies")
    void resolveAndRemember_single() {

        DependentContainer<String, Module> container = SimpleDependentContainer.<String, Module>builder()
                .add(module("1", SemVersionParser.parse("0.0.1")).build())
                .add(module("2", SemVersionParser.parse("0.0.1"))
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any()).build()
                )
                .add(module("3", SemVersionParser.parse("0.0.1"))
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any()).build()
                )
                .add(module("4", SemVersionParser.parse("0.0.1"))
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
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
                .add(module("1", SemVersionParser.parse("0.0.1")).build())
                .add(module("2", SemVersionParser.parse("0.0.1"))
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any()).build()
                )
                .add(module("3", SemVersionParser.parse("0.0.1"))
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any()).build()
                )
                .add(module("4", SemVersionParser.parse("0.0.1"))
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .build()
                )

                .build();

        DependentContainer<String, Module> container2 = SimpleDependentContainer.<String, Module>builder()
                .add(module("5", SemVersionParser.parse("0.0.1")).build())
                .add(module("7", SemVersionParser.parse("0.0.1"))
                        .dependsOn("5", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any()).build()
                )
                .add(module("6", SemVersionParser.parse("0.0.1"))
                        .dependsOn("5", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .dependsOn("7", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .build()
                )

                .build();

        DependentContainer<String, Module> container3 = SimpleDependentContainer.<String, Module>builder()
                .add(module("8", SemVersionParser.parse("0.0.1"))
                        .dependsOn("4", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .dependsOn("6", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
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
                .add(module("1", SemVersionParser.parse("0.0.1")).build())
                .add(module("2", SemVersionParser.parse("0.0.1"))
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any()).build()
                )
                .add(module("3", SemVersionParser.parse("0.0.1"))
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any()).build()
                )
                .add(module("4", SemVersionParser.parse("0.0.1"))
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .build()
                )

                .build();

        DependentContainer<String, Module> container2 = SimpleDependentContainer.<String, Module>builder()
                .add(module("3", SemVersionParser.parse("0.0.1")).build())
                .add(module("1", SemVersionParser.parse("0.0.1"))
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any()).build()
                )
                .add(module("6", SemVersionParser.parse("0.0.1"))
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .dependsOn("4", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
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


        assertTrue(result2.hasDiagnostics(DiagnosticType.ERROR));
        assertThat(result2.unwrapDiagnostics())
                .contains(DependencyDiagnostic.duplicate("1"))
                .contains(DependencyDiagnostic.duplicate("3"));

    }

    @Test
    @DisplayName("resolveAndRemember: missing dependency during resolution of several groups")
    void resolveAndRemember_multipleMissingDependency() {

        DependentContainer<String, Module> container1 = SimpleDependentContainer.<String, Module>builder()
                .add(module("1", SemVersionParser.parse("0.0.1")).build())
                .add(module("2", SemVersionParser.parse("0.0.1"))
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any()).build()
                )
                .add(module("3", SemVersionParser.parse("0.0.1"))
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any()).build()
                )
                .add(module("4", SemVersionParser.parse("0.0.1"))
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .build()
                )

                .build();

        DependentContainer<String, Module> container2 = SimpleDependentContainer.<String, Module>builder()
                .add(module("5", SemVersionParser.parse("0.0.1")).build())
                .add(module("7", SemVersionParser.parse("0.0.1"))
                        .dependsOn("5", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any()).build()
                )
                .add(module("6", SemVersionParser.parse("0.0.1"))
                        .dependsOn("5", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .dependsOn("7", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .build()
                )

                .build();

        DependentContainer<String, Module> container3 = SimpleDependentContainer.<String, Module>builder()
                .add(module("8", SemVersionParser.parse("0.0.1"))
                        .dependsOn("9", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .dependsOn("13", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
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


        assertTrue(result3.hasDiagnostics(DiagnosticType.ERROR));
        assertThat(result3.unwrapDiagnostics())
                .contains(DependencyDiagnostic.missing("8", "9"))
                .contains(DependencyDiagnostic.missing("8", "13"));

    }

    @Test
    @DisplayName("resolveAndRemember: circular dependency during resolution of several groups")
    void resolveAndRemember_multipleCircular() {

        DependentContainer<String, Module> container1 = SimpleDependentContainer.<String, Module>builder()
                .add(module("1", SemVersionParser.parse("0.0.1")).build())
                .add(module("2", SemVersionParser.parse("0.0.1"))
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any()).build()
                )
                .add(module("3", SemVersionParser.parse("0.0.1"))
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any()).build()
                )
                .add(module("4", SemVersionParser.parse("0.0.1"))
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .build()
                )

                .build();

        DependentContainer<String, Module> container2 = SimpleDependentContainer.<String, Module>builder()
                .add(module("5", SemVersionParser.parse("0.0.1"))
                        .dependsOn("6", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any()).build()
                )
                .add(module("6", SemVersionParser.parse("0.0.1"))
                        .dependsOn("5", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
                        .dependsOn("4", DependencyLink.REQUIRED, DependOrder.AFTER, VersionRange.any())
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

        assertTrue(result2.hasDiagnostics(DiagnosticType.ERROR));
        assertThat(result2.unwrapDiagnostics())
                .contains(DependencyDiagnostic.circular(new CyclePath<>("6", List.of("1", "3", "4", "6", "5"))));

    }

    @Test
    @DisplayName("resolveAndRemember: version mismatch during resolution of several groups")
    void resolveAndRemember_multipleVersionMismatch() {

        SemVersion actualVersion = SemVersionParser.parse("0.0.1");
        DependentContainer<String, Module> container1 = SimpleDependentContainer.<String, Module>builder()
                .add(module("2", actualVersion).build())
                .build();

        VersionRange requiredVersionRange = VersionRangeParser.parse(">0.0.2");
        DependentContainer<String, Module> container2 = SimpleDependentContainer.<String, Module>builder()
                .add(module("3", SemVersionParser.parse("0.0.1"))
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, requiredVersionRange).build()
                )

                .build();

        ResolutionResult<String, Module> result1 = resolver.resolveAndRemember(container1).unwrapOrThrow();
        DiagnosticResult<ResolutionResult<String, Module>, DependencyDiagnostic> result2 =
                resolver.resolveAndRemember(container2);

        assertThat(result1)
                .satisfies(r -> {
                    List<Module> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(1)
                            .extracting(Module::id)
                            .containsExactly("2");
                })
                .satisfies(r -> {
                    List<List<Module>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(1);
                });

        assertTrue(result2.hasDiagnostics(DiagnosticType.ERROR));
        assertThat(result2.unwrapDiagnostics())
                .contains(DependencyDiagnostic.versionMismatch("3", "2", requiredVersionRange, actualVersion));

    }

    @Test
    @DisplayName("resolveAndRemember: version conflict during resolution of several groups")
    void resolveAndRemember_multipleVersionConflict() {

        VersionRange range2 = VersionRangeParser.parse(">=0.0.4");
        DependentContainer<String, Module> container1 = SimpleDependentContainer.<String, Module>builder()
                .add(module("1", SemVersionParser.parse("0.1.0")).build())
                .add(module("2", SemVersionParser.parse("0.0.1"))
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, range2).build()
                )
                .build();

        VersionRange range3 = VersionRangeParser.parse("<0.0.2");
        DependentContainer<String, Module> container2 = SimpleDependentContainer.<String, Module>builder()
                .add(module("3", SemVersionParser.parse("0.0.1"))
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, range3).build()
                )

                .build();

        ResolutionResult<String, Module> result1 = resolver.resolveAndRemember(container1).unwrapOrThrow();
        DiagnosticResult<ResolutionResult<String, Module>, DependencyDiagnostic> result2 =
                resolver.resolveAndRemember(container2);

        assertThat(result1)
                .satisfies(r -> {
                    List<Module> ordered = r.ordered();
                    assertThat(ordered)
                            .hasSize(2)
                            .extracting(Module::id)
                            .containsExactly("1", "2");
                })
                .satisfies(r -> {
                    List<List<Module>> layers = r.layers();
                    assertThat(layers)
                            .hasSize(2);
                });

        assertTrue(result2.hasDiagnostics(DiagnosticType.ERROR));
        assertThat(result2.unwrapDiagnostics())
                .contains(DependencyDiagnostic.versionConflict(
                        "1",
                        List.of(
                                new VersionConflictEntry<>("3", range3),
                                new VersionConflictEntry<>("2", range2)
                            )
                        )
                );


    }


}