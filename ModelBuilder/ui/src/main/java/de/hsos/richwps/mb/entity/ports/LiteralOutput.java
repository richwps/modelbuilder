package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;

public class LiteralOutput extends ProcessOutputPort {
    
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


}
