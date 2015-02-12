package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;
import de.hsos.richwps.mb.properties.Property;

public abstract class ProcessInputPort extends ProcessPort {

    public static String TOOLTIP_STYLE_INPUT = "";
    public final static String PROPERTY_KEY_MINOCCURS = "Min occurs";
    public final static String PROPERTY_KEY_MAXOCCURS = "Max occurs";

    public ProcessInputPort() {
        this(null, false);
    }

    protected ProcessInputPort(ProcessPortDatatype processPortDatatype) {
        this(processPortDatatype, false);
    }

    protected ProcessInputPort(ProcessPortDatatype processPortDatatype, boolean global) {
        super(processPortDatatype, global);

        setFlowInput(!global);
        createProperties("");
    }

    @Override
    protected void createProperties(String owsIdentifier) {
        super.createProperties(owsIdentifier);

        Property<Integer> property = new Property<>(PROPERTY_KEY_MINOCCURS, Property.COMPONENT_TYPE_INTEGER, null);
        owsGroup.addObject(property);

        property = new Property<>(PROPERTY_KEY_MAXOCCURS, Property.COMPONENT_TYPE_INTEGER, null);
        owsGroup.addObject(property);
    }

    @Override
    public void setGlobal(boolean global) {
        super.setGlobal(global);
        setFlowInput(!global);
    }

}
