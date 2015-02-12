package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;

public abstract class ProcessOutputPort extends ProcessPort {

    public static String TOOLTIP_STYLE_OUTPUT = "";

    public ProcessOutputPort() {
        this(null, false);
    }

    protected ProcessOutputPort(ProcessPortDatatype processPortDatatype) {
        this(processPortDatatype, false);
    }

    protected ProcessOutputPort(ProcessPortDatatype processPortDatatype, boolean global) {
        super(processPortDatatype, global);

        setFlowOutput(!global);
        createProperties("");
    }

    @Override
    public void setGlobal(boolean global) {
        super.setGlobal(global);
        setFlowOutput(!global);
    }

}
