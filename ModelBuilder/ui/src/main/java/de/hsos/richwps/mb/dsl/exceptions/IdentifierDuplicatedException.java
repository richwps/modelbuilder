package de.hsos.richwps.mb.dsl.exceptions;

/**
 * Thrown during model-to-dsl translation if an port identifier is used multiple
 * times.
 *
 * @author dziegenh
 */
public class IdentifierDuplicatedException extends Exception {

    private final static String msg = "%s port identifier '%s' is used %d times";

    public IdentifierDuplicatedException(String identifier, boolean isInput, int times) {
        super(String.format(msg, (isInput ? "Input" : "Output"), identifier, times));
    }
}
