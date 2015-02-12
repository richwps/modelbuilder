package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;
import static de.hsos.richwps.mb.entity.ProcessPort.PROPERTY_KEY_DATATYPEDESCRIPTION;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionBoundingBox;
import de.hsos.richwps.mb.entity.datatypes.IDataTypeDescription;
import static de.hsos.richwps.mb.entity.ports.BoundingBoxInput.COMPONENTTYPE_DATATYPEDESCRIPTION;
import de.hsos.richwps.mb.exception.IllegalDatatypeDescriptionException;
import de.hsos.richwps.mb.properties.Property;

public class BoundingBoxOutput extends ProcessOutputPort {

    public BoundingBoxOutput() {
        this(false);
    }

    public BoundingBoxOutput(boolean global) {
        super(ProcessPortDatatype.BOUNDING_BOX, global);

        createProperties("");
        globalStatusChanged();
        updateDatatypeDescriptionProperty(COMPONENTTYPE_DATATYPEDESCRIPTION);
    }

    public BoundingBoxOutput clone() {
        BoundingBoxOutput clone = new BoundingBoxOutput(isGlobal());
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
