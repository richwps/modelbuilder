package de.hsos.richwps.mb.richWPS.entity.impl.values;

import de.hsos.richwps.mb.richWPS.entity.IInputValue;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputBoundingBoxDataDescription;

/**
 *
 * @author caduevel
 */
public class InputBoundingBoxDataValue implements IInputValue {

    private InputBoundingBoxDataDescription description;

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
     * @param description
     */
    public InputBoundingBoxDataValue(
            final InputBoundingBoxDataDescription description) {
        this.description = description;
        this.value = "";
    }

    /**
     *
     * @param description
     * @param value Comma-seperated Coordinates.
     */
    public InputBoundingBoxDataValue(
            final InputBoundingBoxDataDescription description, String value) {
        this.description = description;
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
        return this.description.getIdentifier();
    }

    /**
     *
     * @return
     */
    public InputBoundingBoxDataDescription getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(InputBoundingBoxDataDescription description) {
        this.description = description;
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
