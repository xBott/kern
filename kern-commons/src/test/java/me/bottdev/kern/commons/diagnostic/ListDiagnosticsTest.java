package me.bottdev.kern.commons.diagnostic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ListDiagnosticsTest {

    private DiagnosticsBuilder<TestDiagnostic> builder;

    @BeforeEach
    void setUp() {
        builder = ListDiagnostics.builder();
    }

    @Test
    @DisplayName("Builder: is empty initially")
    void builderInitiallyEmpty() {
        assertTrue(builder.isEmpty());
        assertFalse(builder.has(DiagnosticSeverity.INFO));
        assertFalse(builder.has(DiagnosticSeverity.WARN));
        assertFalse(builder.has(DiagnosticSeverity.ERROR));
    }

    @Test
    @DisplayName("Builder: append correctly tracks state")
    void builderTracksAppendedItems() {
        builder.append(TestDiagnostic.info("info message"));
        
        assertFalse(builder.isEmpty());
        assertTrue(builder.has(DiagnosticSeverity.INFO));
        assertFalse(builder.has(DiagnosticSeverity.ERROR));
    }

    @Test
    @DisplayName("ListDiagnostics: empty returns empty diagnostics")
    void emptyReturnsEmpty() {
        ListDiagnostics<Diagnostic> empty = ListDiagnostics.empty();
        assertTrue(empty.isEmpty());
        assertEquals(0, empty.size());
    }

    @Test
    @DisplayName("ListDiagnostics: correctly builds and retrieves diagnostics")
    void buildsAndRetrievesDiagnostics() {
        TestDiagnostic info1 = TestDiagnostic.info("info 1");
        TestDiagnostic warn1 = TestDiagnostic.warn("warn 1");
        TestDiagnostic error1 = TestDiagnostic.error("error 1");
        TestDiagnostic error2 = TestDiagnostic.error("error 2");

        Diagnostics<TestDiagnostic> diagnostics = builder
                .append(info1)
                .append(warn1)
                .append(error1)
                .append(error2)
                .build();

        assertFalse(diagnostics.isEmpty());
        assertEquals(4, diagnostics.size());

        assertTrue(diagnostics.has(DiagnosticSeverity.INFO));
        assertTrue(diagnostics.has(DiagnosticSeverity.WARN));
        assertTrue(diagnostics.has(DiagnosticSeverity.ERROR));

        assertThat(diagnostics.ofType(DiagnosticSeverity.ERROR))
                .containsExactly(error1, error2);

        assertThat(diagnostics.all())
                .containsExactly(info1, warn1, error1, error2);
    }

    @Test
    @DisplayName("ListDiagnostics: grouped correctly groups by type")
    void groupsByType() {
        TestDiagnostic info1 = TestDiagnostic.info("info 1");
        TestDiagnostic error1 = TestDiagnostic.error("error 1");

        Diagnostics<TestDiagnostic> diagnostics = builder
                .append(info1)
                .append(error1)
                .build();

        Map<DiagnosticSeverity, List<TestDiagnostic>> grouped = diagnostics.grouped();

        assertThat(grouped).containsKeys(DiagnosticSeverity.INFO, DiagnosticSeverity.ERROR);
        assertThat(grouped).doesNotContainKey(DiagnosticSeverity.WARN);
        
        assertThat(grouped.get(DiagnosticSeverity.INFO)).containsExactly(info1);
        assertThat(grouped.get(DiagnosticSeverity.ERROR)).containsExactly(error1);
    }

    @Test
    @DisplayName("ListDiagnostics: supports iteration")
    void supportsIteration() {
        TestDiagnostic info1 = TestDiagnostic.info("info 1");
        Diagnostics<TestDiagnostic> diagnostics = builder.append(info1).build();

        int count = 0;
        for (TestDiagnostic d : diagnostics) {
            assertEquals(info1, d);
            count++;
        }
        assertEquals(1, count);
    }
}
