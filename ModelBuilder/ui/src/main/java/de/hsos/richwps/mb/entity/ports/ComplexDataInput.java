package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.properties.Property;

public class ComplexDataInput extends ProcessInputPort {

    public final static String PROPERTY_KEY_MAXMB = "Max MB";
    public static final String PROPERTY_KEY_DATATYPEDESCRIPTION = "Complex datatype";

    public static String COMPONENTTYPE_DATATYPEDESCRIPTION = "Datatype description complex";

    public ComplexDataInput() {
        this(false);
    }

    public ComplexDataInput(boolean global) {
        super(ProcessPortDatatype.COMPLEX, global);

        createProperties("");
        globalStatusChanged();
    }

    @Override
    protected void createProperties(String owsIdentifier) {
        super.createProperties(owsIdentifier);
        
        Property<Integer> property = new Property<>(PROPERTY_KEY_MAXMB, Property.COMPONENT_TYPE_INTEGER, null);
        owsGroup.addObject(property);

        DataTypeDescriptionComplex dataDesc = new DataTypeDescriptionComplex();
        Property<DataTypeDescriptionComplex> descProperty = new Property<>(PROPERTY_KEY_DATATYPEDESCRIPTION, COMPONENTTYPE_DATATYPEDESCRIPTION, dataDesc);
        owsGroup.addObject(descProperty);
    }

    public ComplexDataInput clone() {
        ComplexDataInput clone = new ComplexDataInput(isGlobal());
        super.cloneInto(clone);

        return clone;
    }

}
