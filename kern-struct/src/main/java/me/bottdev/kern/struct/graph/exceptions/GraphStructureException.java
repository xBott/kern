package me.bottdev.kern.struct.graph.exceptions;

/// Thrown when graph structure does not satisfy an operation requirement.
public class GraphStructureException extends GraphException {

    /// Creates an exception with the provided message.
    public GraphStructureException(String message) {
        super(message);
    }

}
