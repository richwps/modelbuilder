package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;
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
    }

    public ComplexDataOutput clone() {
        ComplexDataOutput clone = new ComplexDataOutput(isGlobal());
        super.cloneInto(clone);

        return clone;
    }

}
