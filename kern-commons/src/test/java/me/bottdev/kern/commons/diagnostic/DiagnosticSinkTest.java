package me.bottdev.kern.commons.diagnostic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DiagnosticSinkTest {

    @Test
    @DisplayName("noOp() sink ignores input")
    void noOpIgnoresInput() {
        DiagnosticSink<TestDiagnostic> sink = DiagnosticSink.noOp();
        
        assertDoesNotThrow(() -> sink.accept(TestDiagnostic.info("test")));
    }

    @Test
    @DisplayName("forwarding() passes diagnostic to consumer")
    void forwardingPassesToConsumer() {
        List<TestDiagnostic> received = new ArrayList<>();
        DiagnosticSink<TestDiagnostic> sink = DiagnosticSink.forwarding(received::add);

        TestDiagnostic d = TestDiagnostic.info("test");
        sink.accept(d);

        assertThat(received).containsExactly(d);
    }

    @Test
    @DisplayName("andThen() chains sinks in correct order")
    void andThenChainsSinks() {
        List<String> order = new ArrayList<>();
        
        DiagnosticSink<TestDiagnostic> first = d -> order.add("first: " + d.message());
        DiagnosticSink<TestDiagnostic> second = d -> order.add("second: " + d.message());

        DiagnosticSink<TestDiagnostic> chained = first.andThen(second);
        chained.accept(TestDiagnostic.info("msg"));

        assertThat(order).containsExactly("first: msg", "second: msg");
    }

}
