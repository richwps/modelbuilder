package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionComplex;
import static de.hsos.richwps.mb.entity.ports.ComplexDataInput.COMPONENTTYPE_DATATYPEDESCRIPTION;
import static de.hsos.richwps.mb.entity.ports.ComplexDataInput.PROPERTY_KEY_DATATYPEDESCRIPTION;
import de.hsos.richwps.mb.properties.Property;

public class ComplexDataOutput extends ProcessOutputPort {

    public final static String PROPERTY_KEY_MAXMB = ComplexDataInput.PROPERTY_KEY_MAXMB;
    public final static String PROPERTY_KEY_DATATYPEDESCRIPTION = ComplexDataInput.PROPERTY_KEY_DATATYPEDESCRIPTION;

    public ComplexDataOutput() {
        this(false);
    }

    public ComplexDataOutput(boolean global) {
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

    public ComplexDataOutput clone() {
        ComplexDataOutput clone = new ComplexDataOutput(isGlobal());
        super.cloneInto(clone);

        return clone;
    }

}
