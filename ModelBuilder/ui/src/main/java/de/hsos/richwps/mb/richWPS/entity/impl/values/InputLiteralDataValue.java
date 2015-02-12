package de.hsos.richwps.mb.richWPS.entity.impl.values;

import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputLiteralDataDescription;
import de.hsos.richwps.mb.richWPS.entity.IInputValue;

/**
 *
 * @author dalcacer
 */
public class InputLiteralDataValue implements IInputValue {

    private InputLiteralDataDescription description;
    private String value;

    /**
     *
     * @param description
     */
    public InputLiteralDataValue(InputLiteralDataDescription description) {
        this.description = description;
        this.value = "";
    }

    /**
     *
     * @param description
     * @param value
     */
    public InputLiteralDataValue(InputLiteralDataDescription description, String value) {
        this.description = description;
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
        return this.description.getIdentifier();
    }

}
