package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;

public class BoundingBoxInput extends ProcessInputPort {

    public static String PROPERTY_KEY_DESCRIPTION = "Datatype description bbox";

    public BoundingBoxInput() {
        this(false);
    }

    public BoundingBoxInput(boolean global) {
        super(ProcessPortDatatype.BOUNDING_BOX, global);

        createProperties("");
        globalStatusChanged();
    }

    public BoundingBoxInput clone() {
        BoundingBoxInput clone = new BoundingBoxInput(isGlobal());
        super.cloneInto(clone);

        return clone;
    }

}
