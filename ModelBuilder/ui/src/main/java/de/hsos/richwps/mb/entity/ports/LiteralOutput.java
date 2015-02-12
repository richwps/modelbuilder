package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;
import static de.hsos.richwps.mb.entity.ProcessPort.PROPERTY_KEY_DATATYPEDESCRIPTION;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionLiteral;
import de.hsos.richwps.mb.entity.datatypes.IDataTypeDescription;
import static de.hsos.richwps.mb.entity.ports.LiteralInput.COMPONENTTYPE_DATATYPEDESCRIPTION;
import de.hsos.richwps.mb.exception.IllegalDatatypeDescriptionException;
import de.hsos.richwps.mb.properties.Property;

public class LiteralOutput extends ProcessOutputPort {

    public LiteralOutput() {
        this(false);
    }

    public LiteralOutput(boolean global) {
        super(ProcessPortDatatype.LITERAL, global);

        createProperties("");
        globalStatusChanged();
        updateDatatypeDescriptionProperty(COMPONENTTYPE_DATATYPEDESCRIPTION);
    }

    public LiteralOutput clone() {
        LiteralOutput clone = new LiteralOutput(isGlobal());
        super.cloneInto(clone);

        return clone;
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
