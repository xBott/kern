package me.bottdev.kern.commons.diagnostic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BufferedDiagnosticSinkTest {

    private BufferedDiagnosticSink<TestDiagnostic> sink;

    @BeforeEach
    void setUp() {
        sink = new BufferedDiagnosticSink<>(16);
    }

    @Test
    @DisplayName("Initially empty")
    void initiallyEmpty() {
        assertThat(sink.getDiagnostics()).isEmpty();
        assertFalse(sink.has(DiagnosticSeverity.INFO));
        assertFalse(sink.hasErrors());
    }

    @Test
    @DisplayName("Accumulates accepted diagnostics")
    void accumulatesDiagnostics() {
        TestDiagnostic info = TestDiagnostic.info("info");
        TestDiagnostic warn = TestDiagnostic.warn("warn");

        sink.accept(info);
        sink.accept(warn);

        assertThat(sink.getDiagnostics()).containsExactly(info, warn);
    }

    @Test
    @DisplayName("has: returns true for matching types")
    void hasReturnsTrueForMatchingTypes() {
        sink.accept(TestDiagnostic.warn("warn"));

        assertTrue(sink.has(DiagnosticSeverity.WARN));
        assertFalse(sink.has(DiagnosticSeverity.ERROR));
        assertFalse(sink.has(DiagnosticSeverity.INFO));
    }

    @Test
    @DisplayName("hasErrors: correctly identifies errors")
    void hasErrorsIdentifiesErrors() {
        sink.accept(TestDiagnostic.info("info"));
        assertFalse(sink.hasErrors());

        sink.accept(TestDiagnostic.error("error"));
        assertTrue(sink.hasErrors());
    }

    @Test
    @DisplayName("clear: resets the buffer")
    void clearResetsBuffer() {
        sink.accept(TestDiagnostic.error("error"));
        assertTrue(sink.hasErrors());
        assertThat(sink.getDiagnostics()).hasSize(1);

        sink.clear();

        assertFalse(sink.hasErrors());
        assertThat(sink.getDiagnostics()).isEmpty();
    }

}
