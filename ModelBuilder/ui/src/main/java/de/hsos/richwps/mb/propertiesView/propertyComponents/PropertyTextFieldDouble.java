package de.hsos.richwps.mb.propertiesView.propertyComponents;

import de.hsos.richwps.mb.properties.Property;
import org.apache.commons.lang3.Validate;

/**
 * Swing Component for representing/editing Double properties.
 *
 * @author dziegenh
 */
public class PropertyTextFieldDouble extends PropertyTextField<Double> {

    private String valueViewFormat = "%f";

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

    @Override
    public String getValueForViews() {
        return String.format(valueViewFormat, getValue());
    }

    public void setValueViewFormat(String valueViewFormat) {
        // provoke an exception for invalid formats
        String valid = String.format(valueViewFormat, new Double(1.));
        Validate.notNull(valid);
        
        this.valueViewFormat = valueViewFormat;
    }

}
