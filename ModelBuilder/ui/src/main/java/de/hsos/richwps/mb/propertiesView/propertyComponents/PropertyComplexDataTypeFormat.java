/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.propertiesView.propertyComponents;

import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.propertiesView.AbstractPortCard;
import de.hsos.richwps.mb.semanticProxy.boundary.FormatProvider;
import de.hsos.richwps.mb.semanticProxy.boundary.LoadDataTypesException;
import de.hsos.richwps.mb.ui.ComplexDataTypeFormatLabel;
import java.awt.Component;

/**
 *
 * @author dziegenh
 */
public class PropertyComplexDataTypeFormat extends AbstractPropertyComponent {

    ComplexDataTypeFormat format;
    ComplexDataTypeFormatLabel component;

    public PropertyComplexDataTypeFormat() throws LoadDataTypesException {
        super(AbstractPortCard.PORT_DATATYPE_FORMAT);
         component = new ComplexDataTypeFormatLabel(FormatProvider.getInstance().getComplexDataTypes());
    }

    @Override
    public Object getValue() {
        return format;
    }

    @Override
    public void setValue(Object value) {
        this.format = (ComplexDataTypeFormat) value;
        component.setComplexDataTypeFormat(format);
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public void setEditable(boolean editable) {
        component.setEditable(editable);
    }

}
