package de.hsos.richwps.mb.processProvider.exception;

import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;

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
