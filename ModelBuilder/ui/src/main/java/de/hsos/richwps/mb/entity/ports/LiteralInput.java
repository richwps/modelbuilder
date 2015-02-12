package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;
import static de.hsos.richwps.mb.entity.ProcessPort.PROPERTY_KEY_DATATYPEDESCRIPTION;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionLiteral;
import de.hsos.richwps.mb.entity.datatypes.IDataTypeDescription;
import de.hsos.richwps.mb.exception.IllegalDatatypeDescriptionException;
import de.hsos.richwps.mb.properties.Property;

public class LiteralInput extends ProcessInputPort {

    public static String COMPONENTTYPE_DATATYPEDESCRIPTION = "Datatype description literal";

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

    @Override
    public void setDataTypeDescription(IDataTypeDescription dataTypeDescription) throws IllegalDatatypeDescriptionException {
        if (null != dataTypeDescription && !(dataTypeDescription instanceof DataTypeDescriptionLiteral)) {
            throw new IllegalDatatypeDescriptionException(getDatatype(), dataTypeDescription);
        }

        Property property = owsGroup.getPropertyObject(PROPERTY_KEY_DATATYPEDESCRIPTION);
        property.setValue(dataTypeDescription);
    }

}
