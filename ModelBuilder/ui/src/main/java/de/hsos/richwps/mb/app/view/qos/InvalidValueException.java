package de.hsos.richwps.mb.app.view.qos;

/**
 *
 * @author dziegenh
 */
public class InvalidValueException extends Exception {

    private final static String msgFormat = "Invalid value '%s' for field '%s'";
    
    public InvalidValueException(String field, String value) {
        super(String.format(msgFormat, value, field));
    }
    
}
