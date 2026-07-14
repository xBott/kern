package me.bottdev.kern.struct.graph.exceptions;

/// Base runtime exception for graph errors.
public class GraphException extends RuntimeException {

    /// Creates an exception with the provided message.
    public GraphException(String message) {
        super(message);
    }

}
