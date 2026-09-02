package me.bottdev.kern.dependency.graph;

import me.bottdev.kern.commons.diagnostic.DiagnosticType;
import me.bottdev.kern.commons.wrapper.DiagnosticResult;
import me.bottdev.kern.dependency.*;
import me.bottdev.kern.dependency.Module;
import me.bottdev.kern.dependency.containers.SimpleDependentContainer;
import me.bottdev.kern.struct.algorithms.cycle.SimpleCycleDetector;
import me.bottdev.kern.struct.algorithms.sort.KahnSorter;
import me.bottdev.kern.struct.paths.CyclePath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static me.bottdev.kern.dependency.Module.module;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphVersionedDependencyResolverTest {

    private GraphVersionedDependencyResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new GraphVersionedDependencyResolver(new KahnSorter(new SimpleCycleDetector()));
    }

    @Test
    @DisplayName("resolve: resolves one group of dependencies successfully")
    void resolve_single() {
        DependentContainer<String, Module> container = SimpleDependentContainer.<String, Module>builder()
                .add(module("1", "0.0.1").build())
                .add(module("2", "0.0.1")
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, ">=0.0.1").build()
                )
                .add(module("3", "0.0.1")
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, ">=0.0.1").build()
                )
                .add(module("4", "0.0.1")
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, "*")
                        .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER, "*")
                        .build()
                )
                .build();

        ResolutionResult<String, Module> result = resolver.resolve(container).unwrapOrThrow();

        assertThat(result.ordered())
                .hasSize(4)
                .extracting(Module::id)
                .containsExactly("1", "2", "3", "4");

        assertThat(result.layers()).hasSize(3);
    }

    @Test
    @DisplayName("resolve: reports missing dependency")
    void resolve_missingDependency() {
        DependentContainer<String, Module> container = SimpleDependentContainer.<String, Module>builder()
                .add(module("1", "0.0.1")
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, ">=0.0.1").build()
                )
                .build();

        DiagnosticResult<ResolutionResult<String, Module>, DependencyDiagnostic> result = resolver.resolve(container);

        assertTrue(result.hasDiagnostics(DiagnosticType.ERROR));
        assertThat(result.unwrapDiagnostics())
                .contains(new DependencyDiagnostic.Missing<>("1", "2"));
    }

    @Test
    @DisplayName("resolve: skips missing optional dependency")
    void resolve_missingOptionalDependency() {
        DependentContainer<String, Module> container = SimpleDependentContainer.<String, Module>builder()
                .add(module("1", "0.0.1")
                        .dependsOn("2", DependencyLink.OPTIONAL, DependOrder.AFTER, ">=0.0.1").build()
                )
                .build();

        ResolutionResult<String, Module> result = resolver.resolve(container).unwrapOrThrow();

        assertThat(result.ordered())
                .hasSize(1)
                .extracting(Module::id)
                .containsExactly("1");
    }

    @Test
    @DisplayName("resolve: reports version mismatch")
    void resolve_versionMismatch() {
        DependentContainer<String, Module> container = SimpleDependentContainer.<String, Module>builder()
                .add(module("1", "0.0.1").build())
                .add(module("2", "0.0.1")
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, ">=0.0.2").build()
                )
                .build();

        DiagnosticResult<ResolutionResult<String, Module>, DependencyDiagnostic> result = resolver.resolve(container);

        assertTrue(result.hasDiagnostics(DiagnosticType.ERROR));
        assertThat(result.unwrapDiagnostics())
                .contains(new DependencyDiagnostic.VersionMismatch<>("2", "1", ">=0.0.2", "0.0.1"));
    }

    @Test
    @DisplayName("resolve: reports circular dependency")
    void resolve_circularDependency() {
        DependentContainer<String, Module> container = SimpleDependentContainer.<String, Module>builder()
                .add(module("1", "0.0.1")
                        .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER, "*").build()
                )
                .add(module("2", "0.0.1")
                        .dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER, "*").build()
                )
                .build();

        DiagnosticResult<ResolutionResult<String, Module>, DependencyDiagnostic> result = resolver.resolve(container);

        assertTrue(result.hasDiagnostics(DiagnosticType.ERROR));
        assertThat(result.unwrapDiagnostics())
                .contains(new DependencyDiagnostic.Circular<>(new CyclePath<>(List.of("1", "2", "1"))));
    }
}
