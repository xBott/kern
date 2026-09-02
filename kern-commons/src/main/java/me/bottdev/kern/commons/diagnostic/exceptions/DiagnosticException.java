package me.bottdev.kern.commons.diagnostic.exceptions;

import lombok.Getter;
import me.bottdev.kern.commons.diagnostic.Diagnostic;
import me.bottdev.kern.commons.diagnostic.DiagnosticType;
import me.bottdev.kern.commons.diagnostic.Diagnostics;

import java.util.List;

/// Unchecked aggregate thrown when diagnostics have errors
/// Carries every diagnostic, not just the first one.
@Getter
public class DiagnosticException extends RuntimeException {

    private final Diagnostics diagnostics;

    public DiagnosticException(Diagnostics diagnostics) {
        super(buildMessage(diagnostics));
        this.diagnostics = diagnostics;
    }

    private static String buildMessage(Diagnostics diagnostics) {

        List<Diagnostic> errors = diagnostics.ofType(DiagnosticType.ERROR);

        if (errors.isEmpty()) {
            return "Diagnostics are empty.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Diagnostics contain ")
                .append(errors.size())
                .append(" error(s):\n");

        int index = 1;
        for (Diagnostic diagnostic : errors) {
            sb.append("  ").append(index++).append(") ").append(diagnostic.message()).append("\n");
        }

        return sb.toString();
    }

}