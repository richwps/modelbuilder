package de.hsos.richwps.mb.app.view.properties;

import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.IFormatSelectionListener;
import de.hsos.richwps.mb.processProvider.boundary.FormatProvider;
import de.hsos.richwps.mb.processProvider.exception.LoadDataTypesException;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import de.hsos.richwps.mb.ui.MultilineLabel;
import java.util.LinkedList;
import java.util.List;

/**
 * Property GUI component representing a complex datatype format.
 *
 * @author dziegenh
 */
public class PropertyComplexDataTypeFormat extends AbstractPropertyComponent<ComplexDataTypeFormatLabel, DataTypeDescriptionComplex> {

    ComplexDataTypeFormatLabel component;

    public static String PROPERTY_NAME = "Complex datatype format";
    public static String COMPONENT_TYPE = "Complex datatype format";

    public PropertyComplexDataTypeFormat(FormatProvider formatProvider) throws LoadDataTypesException {
        super(new Property<DataTypeDescriptionComplex>(PROPERTY_NAME, COMPONENT_TYPE));

        // add empty entry as first list element
        List<ComplexDataTypeFormat> formats = new LinkedList<>();
        formats.add(null);
        formats.addAll(formatProvider.getComplexDataTypes());
        component = new ComplexDataTypeFormatLabel(formats);
        component.addSelectionListener(new IFormatSelectionListener() {
            @Override
            public void formatSelected(ComplexDataTypeFormat format) {
                setFormat(format);
            }
        });
    }

    private void setFormat(ComplexDataTypeFormat format) {
        if (null == property.getValue()) {
            property.setValue(new DataTypeDescriptionComplex(format));
        } else {
            property.getValue().setFormat(format);
        }
    }

    @Override
    public DataTypeDescriptionComplex getValue() {
        return property.getValue();
    }

    @Override
    public void setValue(DataTypeDescriptionComplex value) {
        ComplexDataTypeFormat format = null;
        if (null != value) {
            format = value.getFormat();
        }

        component.setComplexDataTypeFormat(format);

        property.setValue(value);
    }

    @Override
    public ComplexDataTypeFormatLabel getComponent() {
        return component;
    }

    @Override
    public void setEditable(boolean editable) {
        component.setEditable(editable);
        property.setEditable(editable);
    }

    @Override
    public void setProperty(Property<DataTypeDescriptionComplex> property) {
        super.setProperty(property);
        setEditable(property.isEditable());
    }

    
    
}
