package de.hsos.richwps.mb.entity.ports;

import de.hsos.richwps.mb.entity.*;
import de.hsos.richwps.mb.properties.Property;

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




//    @Override
//    public String toString() {
//        if (null != getDatatype()) {
//            return getDatatype().name().substring(0, 1).toUpperCase();
//
//        } else if (null == getOwsTitle()) {
//            return "";
//
//        } else if (getOwsTitle().length() < 3) {
//            return getOwsTitle();
//
//        } else {
//            return getOwsTitle().substring(0, 3) + "...";
//        }
//    }
//
//    @Override
//    public String getToolTipText() {
//        if (null == toolTipText) {
//            if (null == getOwsTitle() || null == getOwsAbstract() || null == getOwsIdentifier()) {
//                return "";
//            }
//
//            // TODO update capacity after refactoring! #48
//            // length of vars + 4 characters for datatype + size of "<html></html>" tags + size of "<b></b>" tags + size of "<i></i>" tags + size of "<br>" tags
//            int sbCapacity = getOwsTitle().length() + getOwsIdentifier().length() + getOwsAbstract().length() + 1 + 13 + 7 + 7 + 8; // TODO add size of port texts!
//            StringBuilder sb = new StringBuilder(sbCapacity);
//            sb.append("<html><div style='");
//
//            // set CSS for local ports
//            if (!isGlobal()) {
//                if (isFlowInput()) {
//                    sb.append(ProcessInputPort.TOOLTIP_STYLE_INPUT);
//                } else {
//                    sb.append(ProcessInputPort.TOOLTIP_STYLE_OUTPUT);
//                }
//            }
//            sb.append("'><b>");
//
//            if (null != getDatatype()) {
//                sb.append("[").append(getDatatype().toString().charAt(0)).append("] ");
//            }
//
//            sb.append(getOwsTitle()).append("</b><br>").append(getOwsIdentifier()).append("<br><i>").append(getOwsAbstract()).append("</i></div></html>");
//            toolTipText = sb.toString();
//        }
//
//        return toolTipText;
//    }
//    public ProcessInputPort clone() {
//        ProcessInputPort clone = new ProcessInputPort(datatype, global);
//        super.cloneInto(clone);
//
//        clone.flowInput = flowInput;
//        clone.toolTipText = null; // indicate lazy init.
//
//        // creates missing properties etc.
//        clone.setGlobal(global);
//
//        return clone;
//    }
//    @Override
//    public boolean equals(Object obj) {
//        if (null == obj || !(obj instanceof ProcessInputPort)) {
//            return false;
//        }
//
//        ProcessInputPort other = (ProcessInputPort) obj;
//
//        return other.datatype.equals(this.datatype)
//                && other.flowInput == this.flowInput
//                && other.global == this.global
//                && other.getOwsAbstract().equals(this.getOwsAbstract())
//                && other.getOwsIdentifier().equals(this.getOwsIdentifier())
//                && other.getOwsTitle().equals(this.getOwsTitle());
//
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 7;
//        hash = 13 * hash + Objects.hashCode(this.datatype);
//        hash = 13 * hash + (this.flowInput ? 1 : 0);
//        hash = 13 * hash + (this.global ? 1 : 0);
//        hash = 13 * hash + Objects.hashCode(getOwsAbstract());
//        hash = 13 * hash + Objects.hashCode(getOwsIdentifier());
//        hash = 13 * hash + Objects.hashCode(getOwsTitle());
//        return hash;
//    }
}
