package de.hsos.richwps.mb.richWPS.entity.execute;

import de.hsos.richwps.mb.richWPS.entity.IInputArgument;
import de.hsos.richwps.mb.richWPS.entity.specifier.*;

/**
 *
 * @author dalcacer
 */
public class InputLiteralDataArgument implements IInputArgument{

    private InputLiteralDataSpecifier specifier;
    private String value;

    public InputLiteralDataArgument(InputLiteralDataSpecifier specifier) {
        this.specifier = specifier;
        this.value = "";
    }

    public InputLiteralDataArgument(InputLiteralDataSpecifier specifier, String value) {
        this.specifier = specifier;
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public String getIdentifier() {
        return this.specifier.getIdentifier();
    }
}
