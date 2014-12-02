package de.hsos.richwps.mb.propertiesView.propertyComponents;

import de.hsos.richwps.mb.properties.Property;

/**
 * Simple input for property String values.
 *
 * @author dziegenh
 */
public class PropertyTextFieldString extends PropertyTextField<String> {

    public PropertyTextFieldString() {
    }

    public PropertyTextFieldString(Property<String> property) {
        super(property);
    }

    @Override
    protected String parseValue(String value) {
        return value;
    }

}
