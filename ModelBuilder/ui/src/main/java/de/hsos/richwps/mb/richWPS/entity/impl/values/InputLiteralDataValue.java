package de.hsos.richwps.mb.richWPS.entity.impl.values;

import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputLiteralDataDescription;
import de.hsos.richwps.mb.richWPS.entity.IInputValue;

/**
 *
 * @author dalcacer
 */
public class InputLiteralDataValue implements IInputValue {

    private InputLiteralDataDescription specifier;
    private String value;

    /**
     *
     * @param specifier
     */
    public InputLiteralDataValue(InputLiteralDataDescription specifier) {
        this.specifier = specifier;
        this.value = "";
    }

    /**
     *
     * @param specifier
     * @param value
     */
    public InputLiteralDataValue(InputLiteralDataDescription specifier, String value) {
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
