package me.bottdev.kern.commons.diagnostic;

public record TestDiagnostic(DiagnosticSeverity severity, String type, String message) implements Diagnostic {
    public static TestDiagnostic info(String message) {
        return new TestDiagnostic(DiagnosticSeverity.INFO, "test_info", message);
    }
    
    public static TestDiagnostic warn(String message) {
        return new TestDiagnostic(DiagnosticSeverity.WARN, "test_warn", message);
    }
    
    public static TestDiagnostic error(String message) {
        return new TestDiagnostic(DiagnosticSeverity.ERROR, "test_error", message);
    }
}
