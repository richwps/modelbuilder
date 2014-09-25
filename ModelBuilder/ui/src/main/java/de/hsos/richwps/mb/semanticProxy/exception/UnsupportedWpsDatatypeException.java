package de.hsos.richwps.mb.semanticProxy.exception;

/**
 *
 * @author dziegenh
 */
public class UnsupportedWpsDatatypeException extends Exception {

    public UnsupportedWpsDatatypeException(int dataType) {
        super("InAndOutputForm datatype '"+dataType+"' is not supported");
    }

}
