package de.hsos.richwps.mb.app.view.properties;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.IDataTypeDescriptionChangeListener;
import de.hsos.richwps.mb.exception.IllegalDefaultFormatException;
import de.hsos.richwps.mb.processProvider.boundary.FormatProvider;
import de.hsos.richwps.mb.processProvider.exception.LoadDataTypesException;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import java.awt.Window;
import java.util.LinkedList;
import java.util.List;

/**
 * Property GUI component representing a complex datatype format.
 *
 * @author dziegenh
 */
public class PropertyComponentComplexDataType extends AbstractPropertyComponent<ComplexDataTypeFormatLabel, DataTypeDescriptionComplex> {

    private ComplexDataTypeFormatLabel component;

    public static String PROPERTY_NAME = "Complex datatype format";
    public static String COMPONENT_TYPE = "Complex datatype format";

    
    public PropertyComponentComplexDataType(final Window parent, FormatProvider formatProvider) throws LoadDataTypesException {
        super(new Property<DataTypeDescriptionComplex>(PROPERTY_NAME, COMPONENT_TYPE));

        List<ComplexDataTypeFormat> formats = new LinkedList<>();
        formats.addAll(formatProvider.getComplexDataTypes());
        component = new ComplexDataTypeFormatLabel(parent, formats);
        component.addSelectionListener(new IDataTypeDescriptionChangeListener() {
            @Override
            public void dataTypeDescriptionChanged(DataTypeDescriptionComplex dataTypeDescription) {
                setDatatypeDescription(dataTypeDescription);
            }
        });
    }

    private void setDatatypeDescription(DataTypeDescriptionComplex dataTypeDescription) {
        setValue(dataTypeDescription);
    }

    @Override
    public DataTypeDescriptionComplex getValue() {
        return property.getValue();
    }

    @Override
    public ComplexDataTypeFormatLabel getComponent() {
        return component;
    }

    protected void setEditable(boolean editable) {
        component.setEditable(editable);
        property.setEditable(editable);
    }

    @Override
    public void setProperty(Property<DataTypeDescriptionComplex> property) {
        super.setProperty(property);
        component.setDatatypeDescription(property.getValue());
        setEditable(property.isEditable());
    }

    @Override
    protected void propertyValueChanged() {
        component.setDatatypeDescription(property.getValue());
    }

}
