package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;
import static de.hsos.richwps.mb.entity.ProcessPort.PROPERTY_KEY_DATATYPEDESCRIPTION;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionBoundingBox;
import de.hsos.richwps.mb.entity.datatypes.IDataTypeDescription;
import de.hsos.richwps.mb.exception.IllegalDatatypeDescriptionException;
import de.hsos.richwps.mb.properties.Property;

public class BoundingBoxInput extends ProcessInputPort {

    public static String COMPONENTTYPE_DATATYPEDESCRIPTION = "Datatype description bbox";

    public BoundingBoxInput() {
        this(false);
    }

    public BoundingBoxInput(boolean global) {
        super(ProcessPortDatatype.BOUNDING_BOX, global);

        createProperties("");
        owsPropertiesCreated();
        updateDatatypeDescriptionProperty(COMPONENTTYPE_DATATYPEDESCRIPTION);
    }

    public BoundingBoxInput clone() {
        BoundingBoxInput clone = new BoundingBoxInput(isGlobal());
        super.cloneInto(clone);

        return clone;
    }

    
    @Override
    public void setDataTypeDescription(IDataTypeDescription dataTypeDescription) throws IllegalDatatypeDescriptionException {
        if (null != dataTypeDescription && !(dataTypeDescription instanceof DataTypeDescriptionBoundingBox)) {
            throw new IllegalDatatypeDescriptionException(getDatatype(), dataTypeDescription);
        }

        Property property = owsGroup.getPropertyObject(PROPERTY_KEY_DATATYPEDESCRIPTION);
        property.setValue(dataTypeDescription);
    }
    
}
