package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;
import de.hsos.richwps.mb.properties.Property;
import java.util.List;

public class LiteralInput extends ProcessInputPort {

    public static String PROPERTY_KEY_DEFAULTVALUE = "Default literal value";
    
    public static final String PROPERTY_KEY_LITERALDATATYPE = "Literal Datatype";
    
    /**
     * Default value for literal datatypes
     */
    public static String DEFAULT_DATATYPE = "xs:string";
    
    public LiteralInput() {
        this(false);
    }

    public LiteralInput(boolean global) {
        super(ProcessPortDatatype.LITERAL, global);

        createProperties("");
        globalStatusChanged();
    }

    public LiteralInput clone() {
        LiteralInput clone = new LiteralInput(isGlobal());
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
        
        Property<String> defaultValue = new Property<>(PROPERTY_KEY_DEFAULTVALUE, Property.COMPONENT_TYPE_TEXTFIELD, null);
        this.owsGroup.addObject(defaultValue);
    }

}
