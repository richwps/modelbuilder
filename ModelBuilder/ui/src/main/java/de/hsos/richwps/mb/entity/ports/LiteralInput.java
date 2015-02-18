package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;
import static de.hsos.richwps.mb.entity.ProcessPort.PROPERTY_KEY_DATATYPEDESCRIPTION;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionLiteral;
import de.hsos.richwps.mb.entity.datatypes.IDataTypeDescription;
import de.hsos.richwps.mb.exception.IllegalDatatypeDescriptionException;
import de.hsos.richwps.mb.properties.Property;
import java.util.List;

public class LiteralInput extends ProcessInputPort {

    public static String COMPONENTTYPE_DATATYPEDESCRIPTION = "Datatype description literal";
    public static final String LITERAL_DATATYPE = "Literal Datatype";
    
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
        updateDatatypeDescriptionProperty(COMPONENTTYPE_DATATYPEDESCRIPTION);

        globalStatusChanged();
    }

    public LiteralInput clone() {
        LiteralInput clone = new LiteralInput(isGlobal());
        super.cloneInto(clone);

        return clone;
    }
    
    public void setDatatypes(List<String> datatypes) {
        Property property = this.owsGroup.getPropertyObject(LITERAL_DATATYPE);
        property.setPossibleValues(datatypes);
    }

    @Override
    protected void createProperties(String owsIdentifier) {
        super.createProperties(owsIdentifier);
        
        Property<String> datatype = new Property<>(LITERAL_DATATYPE, Property.COMPONENT_TYPE_DROPDOWN);
        datatype.setPossibleValuesTransient(true);
        datatype.setValue(DEFAULT_DATATYPE);
        this.owsGroup.addObject(datatype);
    }
    

    @Override
    public void setDataTypeDescription(IDataTypeDescription dataTypeDescription) throws IllegalDatatypeDescriptionException {
        if (null != dataTypeDescription && !(dataTypeDescription instanceof DataTypeDescriptionLiteral)) {
            throw new IllegalDatatypeDescriptionException(getDatatype(), dataTypeDescription);
        }

        Property property = owsGroup.getPropertyObject(PROPERTY_KEY_DATATYPEDESCRIPTION);
        property.setValue(dataTypeDescription);
    }

}
