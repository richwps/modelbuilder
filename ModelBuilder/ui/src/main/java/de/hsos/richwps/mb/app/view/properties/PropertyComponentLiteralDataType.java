package de.hsos.richwps.mb.app.view.properties;

import de.hsos.richwps.mb.entity.DataTypeDescriptionLiteral;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.propertiesView.propertyComponents.PropertyTextField;

/**
 *
 * @author dziegenh
 */
public class PropertyComponentLiteralDataType extends PropertyTextField<DataTypeDescriptionLiteral> {

    public PropertyComponentLiteralDataType() {
        super();
    }

    public PropertyComponentLiteralDataType(Property property) {
        super(property);
    }

    @Override
    protected DataTypeDescriptionLiteral parseValue(String value) {
        return new DataTypeDescriptionLiteral(value);
    }
    
}
