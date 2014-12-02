package de.hsos.richwps.mb.propertiesView.propertyComponents;

import de.hsos.richwps.mb.properties.Property;

/**
 * Swing Component for representing/editing Double properties.
 *
 * @author dziegenh
 */
public class PropertyTextFieldDouble extends PropertyTextField<Double> {

    public PropertyTextFieldDouble(Property<Double> property) {
        super(property);
    }

    @Override
    protected Double parseValue(String value) {
        if (null == value) {
            return null;
        }

        double parsedValue;
        try {
            parsedValue = Double.parseDouble(value.trim());

        } catch (NumberFormatException ex) {
            if (null == property.getValue()) {
                return null;
            }

            parsedValue = property.getValue();
        }

        return parsedValue;
    }

}
