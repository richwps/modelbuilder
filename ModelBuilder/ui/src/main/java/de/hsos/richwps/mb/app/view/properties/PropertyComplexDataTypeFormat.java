package de.hsos.richwps.mb.app.view.properties;

import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.IFormatSelectionListener;
import de.hsos.richwps.mb.exception.IllegalDefaultFormatException;
import de.hsos.richwps.mb.processProvider.boundary.FormatProvider;
import de.hsos.richwps.mb.processProvider.exception.LoadDataTypesException;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import java.awt.Window;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Property GUI component representing a complex datatype format.
 *
 * @author dziegenh
 */
public class PropertyComplexDataTypeFormat extends AbstractPropertyComponent<ComplexDataTypeFormatLabel, DataTypeDescriptionComplex> {

    ComplexDataTypeFormatLabel component;

    public static String PROPERTY_NAME = "Complex datatype format";
    public static String COMPONENT_TYPE = "Complex datatype format";

    public PropertyComplexDataTypeFormat(final Window parent, FormatProvider formatProvider) throws LoadDataTypesException {
        super(new Property<DataTypeDescriptionComplex>(PROPERTY_NAME, COMPONENT_TYPE));

        List<ComplexDataTypeFormat> formats = new LinkedList<>();
        formats.addAll(formatProvider.getComplexDataTypes());
        component = new ComplexDataTypeFormatLabel(parent, formats);
        component.addSelectionListener(new IFormatSelectionListener() {
            @Override
            public void formatSelected(List<ComplexDataTypeFormat> formats) {
                try {
                    setFormats(formats);
                } catch (IllegalDefaultFormatException ex) {
                    // ??
                }
            }
        });
    }

    private void setFormats(List<ComplexDataTypeFormat> formats) throws IllegalDefaultFormatException {

        // set supported formats
        if (null == property.getValue()) {
            property.setValue(new DataTypeDescriptionComplex(formats));
        } else {
            property.getValue().setFormats(formats);
        }

        // set default format 
        // TODO get selected default format which is to be set !!
        if (null != formats && formats.size() > 0) {
            // workaround: default is the first enty 
            property.getValue().setDefaultFormat(formats.get(0));
        } else {
            property.getValue().setDefaultFormat(null);
        }
    }

    @Override
    public DataTypeDescriptionComplex getValue() {
        return property.getValue();
    }

    @Override
    public void setValue(DataTypeDescriptionComplex value) {
        List<ComplexDataTypeFormat> formats = null;
        if (null != value) {
            formats = value.getFormats();
        }

        component.setSelectedFormats(formats);

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
