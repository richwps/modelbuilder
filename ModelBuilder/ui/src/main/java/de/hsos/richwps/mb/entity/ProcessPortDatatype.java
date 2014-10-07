package de.hsos.richwps.mb.entity;

import de.hsos.richwps.mb.ui.UiHelper;

/**
 * The datatypes a port can have.
 *
 * @author dziegenh
 */
public enum ProcessPortDatatype {

    LITERAL,
    COMPLEX,
    BOUNDING_BOX;

    @Override
    public String toString() {
        return UiHelper.upperFirst(name());
    }

    /**
     * Finds an enum value by its name.
     *
     * @param name
     * @return enum value on success or else null.
     */
    public static ProcessPortDatatype getValueByName(String name) {
        for (ProcessPortDatatype value : ProcessPortDatatype.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }

        return null;
    }
}
