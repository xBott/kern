package me.bottdev.kern.struct.graph.exceptions;

/// Thrown when a self-loop edge violates graph rules.
public class GraphSelfLoopException extends RuntimeException {

    /// Creates an exception with the provided message.
    public GraphSelfLoopException(String message) {
        super(message);
    }

}
