package de.hsos.richwps.mb.entity;

import de.hsos.richwps.mb.entity.datatypes.IDataTypeDescription;
import de.hsos.richwps.mb.exception.IllegalDatatypeDescriptionException;
import de.hsos.richwps.mb.properties.Property;
import java.util.Objects;

/**
 * A port entitiy can either be global (model-wide) or local (part of a
 * process).
 *
 * @author dziegenh
 */
public abstract class ProcessPort extends OwsObjectWithProperties {

    public static String TOOLTIP_STYLE_INPUT = "";
    public static String TOOLTIP_STYLE_OUTPUT = "";

    public final static String PROPERTY_KEY_DATATYPEDESCRIPTION = "Datatype description";

    private ProcessPortDatatype datatype;

    private boolean flowInput;

    protected boolean global;

    public ProcessPort() {
        this(null, false);
    }

    public ProcessPort(ProcessPortDatatype processPortDatatype) {
        this(processPortDatatype, false);
    }

    public ProcessPort(ProcessPortDatatype processPortDatatype, boolean global) {
        this.global = global;
        this.datatype = processPortDatatype;

        createProperties("");
    }

    public abstract void setDataTypeDescription(IDataTypeDescription dataTypeDescription)
            throws IllegalDatatypeDescriptionException;

    @Override
    protected void createProperties(String owsIdentifier) {
        super.createProperties(owsIdentifier);

        // create missing properties if necessary
        if (!owsGroup.hasProperty(PROPERTY_KEY_DATATYPEDESCRIPTION)) {
            Property descriptionProperty = new Property<>(PROPERTY_KEY_DATATYPEDESCRIPTION, (IDataTypeDescription) null, global);
            owsGroup.addObject(descriptionProperty);
        }
    }

    public IDataTypeDescription getDataTypeDescription() {
        return (IDataTypeDescription) getPropertyValue(PROPERTY_KEY_DATATYPEDESCRIPTION);
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
        globalStatusChanged();
    }

    protected void globalStatusChanged() {
        for (Property property : owsGroup.getProperties()) {
            if (null != property) {
                property.setEditable(isGlobal());
            }
        }
    }

    /**
     * @return the data
     */
    public ProcessPortDatatype getDatatype() {
        return datatype;
    }

    protected void setFlowInput(boolean isInput) {
        flowInput = isInput;
    }

    /**
     * True if the port receives data.
     *
     * @return
     */
    public boolean isFlowInput() {
        return flowInput;
    }

    /**
     * True if the port sends data.
     *
     * @return
     */
    public boolean isFlowOutput() {
        return !flowInput;
    }

    /**
     * A global process input is a local flow output.
     *
     * @return
     */
    public boolean isGlobalInput() {
        return global && !isFlowInput();
    }

    /**
     * A global process output is a local flow input.
     *
     * @return
     */
    public boolean isGlobalOutput() {
        return global && !isFlowOutput();
    }

    protected void setFlowOutput(boolean isOutput) {
        flowInput = !isOutput;
    }

    protected void updateDatatypeDescriptionProperty(String componentType) {
        Property property = owsGroup.getPropertyObject(PROPERTY_KEY_DATATYPEDESCRIPTION);
        property.setComponentType(componentType);
    }

    @Override
    public String toString() {
        if (null != getDatatype()) {
            return getDatatype().name().substring(0, 1).toUpperCase();

        } else if (null == getOwsTitle()) {
            return "";

        } else if (getOwsTitle().length() < 3) {
            return getOwsTitle();

        } else {
            return getOwsTitle().substring(0, 3) + "...";
        }
    }

    @Override
    public String getToolTipText() {
        if (null == toolTipText) {
            if (null == getOwsTitle() || null == getOwsAbstract() || null == getOwsIdentifier()) {
                return "";
            }

            // TODO update capacity after refactoring! #48
            // length of vars + 4 characters for datatype + size of "<html></html>" tags + size of "<b></b>" tags + size of "<i></i>" tags + size of "<br>" tags
            int sbCapacity = getOwsTitle().length() + getOwsIdentifier().length() + getOwsAbstract().length() + 1 + 13 + 7 + 7 + 8; // TODO add size of port texts!
            StringBuilder sb = new StringBuilder(sbCapacity);
            sb.append("<html><div style='");

            // set CSS for local ports
            if (!isGlobal()) {
                if (isFlowInput()) {
                    sb.append(ProcessPort.TOOLTIP_STYLE_INPUT);
                } else {
                    sb.append(ProcessPort.TOOLTIP_STYLE_OUTPUT);
                }
            }
            sb.append("'><b>");

            if (null != getDatatype()) {
                sb.append("[").append(getDatatype().toString().charAt(0)).append("] ");
            }

            sb.append(getOwsTitle()).append("</b><br>").append(getOwsIdentifier()).append("<br><i>").append(getOwsAbstract()).append("</i></div></html>");
            toolTipText = sb.toString();
        }

        return toolTipText;
    }

    // must be implemented by sub classes. these may call cloneInto().
    public abstract ProcessPort clone();

    public void cloneInto(ProcessPort clone) {
        super.cloneInto(clone);

        clone.flowInput = flowInput;
        clone.toolTipText = null; // indicate lazy init.

        // creates missing properties etc.
        clone.setGlobal(global);
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof ProcessPort)) {
            return false;
        }

        ProcessPort other = (ProcessPort) obj;

        return other.datatype.equals(this.datatype)
                && other.flowInput == this.flowInput
                && other.global == this.global
                && other.getOwsAbstract().equals(this.getOwsAbstract())
                && other.getOwsIdentifier().equals(this.getOwsIdentifier())
                && other.getOwsTitle().equals(this.getOwsTitle());

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.datatype);
        hash = 13 * hash + (this.flowInput ? 1 : 0);
        hash = 13 * hash + (this.global ? 1 : 0);
        hash = 13 * hash + Objects.hashCode(getOwsAbstract());
        hash = 13 * hash + Objects.hashCode(getOwsIdentifier());
        hash = 13 * hash + Objects.hashCode(getOwsTitle());
        return hash;
    }

}
