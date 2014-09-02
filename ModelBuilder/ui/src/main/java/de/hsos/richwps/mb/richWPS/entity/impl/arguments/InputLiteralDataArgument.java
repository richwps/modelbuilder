package de.hsos.richwps.mb.richWPS.entity.impl.arguments;

import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputLiteralDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IInputArgument;

/**
 *
 * @author dalcacer
 */
public class InputLiteralDataArgument implements IInputArgument{

    private InputLiteralDataSpecifier specifier;
    private String value;

    /**
     *
     * @param specifier
     */
    public InputLiteralDataArgument(InputLiteralDataSpecifier specifier) {
        this.specifier = specifier;
        this.value = "";
    }

    /**
     *
     * @param specifier
     * @param value
     */
    public InputLiteralDataArgument(InputLiteralDataSpecifier specifier, String value) {
        this.specifier = specifier;
        this.value = value;
    }

    /**
     *
     * @return
     */
    public String getValue() {
        return this.value;
    }

    /**
     *
     * @return
     */
    public String getIdentifier() {
        return this.specifier.getIdentifier();
    }
}
