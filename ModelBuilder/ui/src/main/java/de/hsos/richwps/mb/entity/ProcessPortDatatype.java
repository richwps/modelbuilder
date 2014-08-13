/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.entity;

public enum ProcessPortDatatype {

    LITERAL,
    COMPLEX,
    BOUNDING_BOX;

    @Override
    public String toString() {
        return new StringBuilder(50)
                .append(name().substring(0, 1)) // upper first
                .append(name().substring(1).toLowerCase()).toString();  // lower others
    }

    /**
     * Finds an enum value by its name.
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
