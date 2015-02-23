package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;
import static de.hsos.richwps.mb.entity.ports.LiteralInput.PROPERTY_KEY_LITERALDATATYPE;
import de.hsos.richwps.mb.properties.Property;
import java.util.List;

public class LiteralOutput extends ProcessOutputPort {

    public static String DEFAULT_DATATYPE = LiteralInput.DEFAULT_DATATYPE;
    public final static String PROPERTY_KEY_LITERALDATATYPE = LiteralInput.PROPERTY_KEY_LITERALDATATYPE;

    public LiteralOutput() {
        this(false);
    }

    public LiteralOutput(boolean global) {
        super(ProcessPortDatatype.LITERAL, global);

        createProperties("");
        globalStatusChanged();
    }

    public LiteralOutput clone() {
        LiteralOutput clone = new LiteralOutput(isGlobal());
        super.cloneInto(clone);

        return clone;
    }

    public void setDatatypes(List<String> datatypes) {
        Property property = this.owsGroup.getPropertyObject(PROPERTY_KEY_LITERALDATATYPE);
        property.setPossibleValues(datatypes);
    }

    @Override
    protected void createProperties(String owsIdentifier) {
        super.createProperties(owsIdentifier);

        Property<String> datatype = new Property<>(PROPERTY_KEY_LITERALDATATYPE, Property.COMPONENT_TYPE_DROPDOWN, null);
        datatype.setPossibleValuesTransient(true);
        datatype.setValue(DEFAULT_DATATYPE);
        this.owsGroup.addObject(datatype);
    }

    @Override
    protected void globalStatusChanged() {
        Property property = this.owsGroup.getPropertyObject(PROPERTY_KEY_LITERALDATATYPE);

        String componentType = Property.COMPONENT_TYPE_DROPDOWN;
        if (!isGlobal()) {
            componentType = Property.COMPONENT_TYPE_TEXTFIELD;
            property.setPossibleValues(null);
        }

        property.setComponentType(componentType);
        
        super.globalStatusChanged();
    }
}
