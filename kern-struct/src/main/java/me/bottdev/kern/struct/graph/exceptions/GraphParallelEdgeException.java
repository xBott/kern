package me.bottdev.kern.struct.graph.exceptions;

/// Thrown when a parallel edge violates graph rules.
public class GraphParallelEdgeException extends RuntimeException {

    /// Creates an exception with the provided message.
    public GraphParallelEdgeException(String message) {
        super(message);
    }

}
