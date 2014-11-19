package de.hsos.richwps.mb.propertiesView.propertyComponents;

import de.hsos.richwps.mb.properties.Property;

/**
 *
 * @author dziegenh
 */
public class PropertyTextFieldInteger extends PropertyTextField<Integer> {

    public PropertyTextFieldInteger(Property<Integer> property) {
        super(property);
    }

    @Override
    protected Integer parseValue(String value) {
        if (null == value) {
            return null;
        }

        int intValue;
        try {
            intValue = Integer.parseInt(value.trim());
            
        } catch (NumberFormatException ex) {
            if (null == property.getValue()) {
                return null;
            }

            intValue = property.getValue();
        }

        return intValue;
    }

}
