package de.hsos.richwps.mb.properties.propertyComponents;

import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.IFormatSelectionListener;
import de.hsos.richwps.mb.processProvider.boundary.FormatProvider;
import de.hsos.richwps.mb.processProvider.exception.LoadDataTypesException;
import de.hsos.richwps.mb.properties.AbstractPropertyComponent;
import de.hsos.richwps.mb.ui.ComplexDataTypeFormatLabel;
import java.util.LinkedList;
import java.util.List;

/**
 * Property GUI component representing a complex datatype format.
 *
 * @author dziegenh
 */
public class PropertyComplexDataTypeFormat extends AbstractPropertyComponent<ComplexDataTypeFormatLabel, DataTypeDescriptionComplex> {

    ComplexDataTypeFormat format;
    ComplexDataTypeFormatLabel component;

    public static String PROPERTY_NAME = "Complex datatype format";

    public PropertyComplexDataTypeFormat() throws LoadDataTypesException {
        super(PROPERTY_NAME);

        // add empty entry as first list element
        List<ComplexDataTypeFormat> formats = new LinkedList<>();
        formats.add(null);
        formats.addAll(FormatProvider.getInstance().getComplexDataTypes());
        component = new ComplexDataTypeFormatLabel(formats);
        component.addSelectionListener(new IFormatSelectionListener() {
            @Override
            public void formatSelected(ComplexDataTypeFormat format) {
                setFormat(format);
            }
        });
    }

    private void setFormat(ComplexDataTypeFormat format) {
        this.format = format;
    }

    @Override
    public DataTypeDescriptionComplex getValue() {
        return new DataTypeDescriptionComplex(format);
    }

    @Override
    public void setValue(DataTypeDescriptionComplex value) {
        if (null == value) {
            this.format = null;
        } else {
            this.format = value.getFormat();
        }

        component.setComplexDataTypeFormat(this.format);
    }

    @Override
    public ComplexDataTypeFormatLabel getComponent() {
        return component;
    }

    @Override
    public void setEditable(boolean editable) {
        component.setEditable(editable);
    }

}
