package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.datatypes.IDataTypeDescription;
import de.hsos.richwps.mb.exception.IllegalDatatypeDescriptionException;
import de.hsos.richwps.mb.properties.Property;

public class ComplexDataInput extends ProcessInputPort {

    public final static String PROPERTY_KEY_MAXMB = "Max MB";

    public static String COMPONENTTYPE_DATATYPEDESCRIPTION = "Datatype description complex";

    public ComplexDataInput() {
        this(false);
    }

    public ComplexDataInput(boolean global) {
        super(ProcessPortDatatype.COMPLEX, global);

        createProperties("");
        globalStatusChanged();
        updateDatatypeDescriptionProperty(COMPONENTTYPE_DATATYPEDESCRIPTION);
    }

    @Override
    protected void createProperties(String owsIdentifier) {
        super.createProperties(owsIdentifier);

        Property<Integer> property = new Property<>(PROPERTY_KEY_MAXMB, Property.COMPONENT_TYPE_INTEGER, null);
        owsGroup.addObject(property);
    }

    public ComplexDataInput clone() {
        ComplexDataInput clone = new ComplexDataInput(isGlobal());
        super.cloneInto(clone);

        return clone;
    }

    @Override
    public void setDataTypeDescription(IDataTypeDescription dataTypeDescription) throws IllegalDatatypeDescriptionException {
        if (null != dataTypeDescription && !(dataTypeDescription instanceof DataTypeDescriptionComplex)) {
            throw new IllegalDatatypeDescriptionException(getDatatype(), dataTypeDescription);
        }

        Property property = owsGroup.getPropertyObject(PROPERTY_KEY_DATATYPEDESCRIPTION);
        property.setValue(dataTypeDescription);

    }

}
