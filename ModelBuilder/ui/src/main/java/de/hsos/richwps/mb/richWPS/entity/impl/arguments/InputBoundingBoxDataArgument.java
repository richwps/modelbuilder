package de.hsos.richwps.mb.richWPS.entity.impl.arguments;

import de.hsos.richwps.mb.richWPS.entity.IInputArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputBoundingBoxDataSpecifier;
import java.io.Serializable;

/**
 *
 * @author caduevel
 */
public class InputBoundingBoxDataArgument implements IInputArgument, Serializable{

    private InputBoundingBoxDataSpecifier specifier;

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
    public InputBoundingBoxDataArgument(
            final InputBoundingBoxDataSpecifier specifier) {
        this.specifier = specifier;
        this.value = "";
    }

    /**
     *
     * @param specifier
     * @param value Comma-seperated Coordinates.
     */
    public InputBoundingBoxDataArgument(
            final InputBoundingBoxDataSpecifier specifier, String value) {
        this.specifier = specifier;
        this.value = value;
        this.crsType= "";
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
     * @return Unambiguous identifier or name of a process, input, or
     * output, unique for this server.
     */
    public String getIdentifier() {
        return this.specifier.getIdentifier();
    }

    /**
     *
     * @return
     */
    public InputBoundingBoxDataSpecifier getSpecifier() {
        return specifier;
    }

    /**
     *
     * @param specifier
     */
    public void setSpecifier(InputBoundingBoxDataSpecifier specifier) {
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
