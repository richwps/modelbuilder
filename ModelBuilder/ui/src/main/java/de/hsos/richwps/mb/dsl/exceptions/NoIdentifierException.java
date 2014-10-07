package de.hsos.richwps.mb.dsl.exceptions;

/**
 * Thrown during model-to-dsl translation if a global port has no identifier.
 *
 * @author dziegenh
 */
public class NoIdentifierException extends Exception {

    private final static String msg = "At least one global %s port has no identifier";

    public NoIdentifierException(boolean isInput) {
        super(String.format(msg, (isInput ? "input" : "output")));
    }

}
