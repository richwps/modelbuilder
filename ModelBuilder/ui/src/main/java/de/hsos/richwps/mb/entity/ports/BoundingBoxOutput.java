package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;

public class BoundingBoxOutput extends ProcessOutputPort {

    public BoundingBoxOutput() {
        this(false);
    }

    public BoundingBoxOutput(boolean global) {
        super(ProcessPortDatatype.BOUNDING_BOX, global);

        createProperties("");
        globalStatusChanged();
    }

    public BoundingBoxOutput clone() {
        BoundingBoxOutput clone = new BoundingBoxOutput(isGlobal());
        super.cloneInto(clone);

        return clone;
    }
    
}
