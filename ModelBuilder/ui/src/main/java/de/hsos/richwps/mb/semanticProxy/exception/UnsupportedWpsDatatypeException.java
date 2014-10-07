package de.hsos.richwps.mb.semanticProxy.exception;

import de.hsos.richwps.mb.semanticProxy.boundary.ProcessProvider;

/**
 * Is thrown by the ProcessProvider if an unsupported datatype occurs.
 *
 * @see ProcessProvider
 * @author dziegenh
 */
public class UnsupportedWpsDatatypeException extends Exception {

    public UnsupportedWpsDatatypeException(int dataType) {
        super("InAndOutputForm datatype '" + dataType + "' is not supported");
    }

}
