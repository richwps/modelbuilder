package de.hsos.richwps.mb.richWPS.entity.impl.values;

import de.hsos.richwps.mb.richWPS.entity.IInputValue;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputBoundingBoxDataDescription;

/**
 *
 * @author caduevel
 */
public class InputBoundingBoxDataValue implements IInputValue {

    private InputBoundingBoxDataDescription specifier;

    /**
     * Comma-seperated Coordinates.
     */
    private String value;
    /**
     * Reference to definition of the CRS used by the coordinates.
     */

    private String crsType;

    /**
     *
     * @param specifier
     */
    public InputBoundingBoxDataValue(
            final InputBoundingBoxDataDescription specifier) {
        this.specifier = specifier;
        this.value = "";
    }

    /**
     *
     * @param specifier
     * @param value Comma-seperated Coordinates.
     */
    public InputBoundingBoxDataValue(
            final InputBoundingBoxDataDescription specifier, String value) {
        this.specifier = specifier;
        this.value = value;
        this.crsType = "";
    }

    /**
     *
     * @return Comma-seperated Coordinates.
     */
    public String getValue() {
        return this.value;
    }

    /**
     *
     * @return Unambiguous identifier or name of a process, input, or output,
     * unique for this server.
     */
    public String getIdentifier() {
        return this.specifier.getIdentifier();
    }

    /**
     *
     * @return
     */
    public InputBoundingBoxDataDescription getSpecifier() {
        return specifier;
    }

    /**
     *
     * @param specifier
     */
    public void setSpecifier(InputBoundingBoxDataDescription specifier) {
        this.specifier = specifier;
    }

    /**
     *
     * @return Reference to definition of the CRS used by the coordinates.
     */
    public String getCrsType() {
        return crsType;
    }

    /**
     *
     * @param crsType Reference to definition of the CRS used by the
     * coordinates.
     */
    public void setCrsType(String crsType) {
        this.crsType = crsType;
    }

}
