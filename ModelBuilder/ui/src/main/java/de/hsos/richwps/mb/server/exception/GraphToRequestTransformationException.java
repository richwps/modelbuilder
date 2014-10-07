package de.hsos.richwps.mb.server.exception;

/**
 * Thrown during deployment if the graph (model) cannot be transformed to a
 * request object.
 *
 * @author dziegenh
 */
public class GraphToRequestTransformationException extends Exception {

    public GraphToRequestTransformationException(Object source) {
        super("Error transforming graph to request object caused by '" + source + "'");
    }

}
