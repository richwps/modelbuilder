package de.hsos.richwps.mb.processProvider.exception;

import de.hsos.richwps.mb.processProvider.boundary.FormatProvider;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProviderConfig;

/**
 * Thrown if an SpClient method is called but SpClient is not available.
 *
 * @see FormatProvider
 * @author dziegenh
 */
public class SpClientNotAvailableException extends Exception {

    public SpClientNotAvailableException() {
        super(ProcessProviderConfig.EXCEPTION_MSG_SP_CLIENT_NOT_AVAILABLE);
    }

    
    
}
