package de.hsos.richwps.mb.exception;

import de.hsos.richwps.mb.app.AppConstants;

/**
 *
 * @author dziegenh
 */
public class IllegalDefaultFormatException extends Exception {

    public IllegalDefaultFormatException() {
        this("", null);
    }

    public IllegalDefaultFormatException(Throwable cause) {
        this("", cause);
    }
    
    public IllegalDefaultFormatException(String message, Throwable cause) {
        super(AppConstants.EXCEPTION_ILLEGAL_DEFAULT_FORMAT, cause);
    }
    
    
    
    
    
}
