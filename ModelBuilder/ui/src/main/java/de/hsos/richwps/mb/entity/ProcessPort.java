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

    private boolean global;

    
    
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
//        updateDescriptionProperty(processPortDatatype);
    }

    public abstract void setDataTypeDescription(IDataTypeDescription dataTypeDescription)
            throws IllegalDatatypeDescriptionException;

//    public static Property createPortProperty(final String propertyKey) {
//        Property created = null;
//
//        switch (propertyKey) {
//            case PROPERTY_KEY_MINOCCURS:
//                created = new Property<Integer>(PROPERTY_KEY_MINOCCURS, Property.COMPONENT_TYPE_INTEGER, null);
//                break;
//
//            case PROPERTY_KEY_MAXOCCURS:
//                created = new Property<Integer>(PROPERTY_KEY_MAXOCCURS, Property.COMPONENT_TYPE_INTEGER, null);
//                break;
//
//            case PROPERTY_KEY_MAXMB:
//                created = new Property<Integer>(PROPERTY_KEY_MAXMB, Property.COMPONENT_TYPE_INTEGER, null);
//                break;
//        }
//        return created;
//    }
//
    @Override
    protected void createProperties(String owsIdentifier) {
        super.createProperties(owsIdentifier);
//
//        boolean hasMinMaxOccurs = false;
//        boolean hasMaxMb = false;
//
        // create missing properties if necessary
        if (!owsGroup.hasProperty(PROPERTY_KEY_DATATYPEDESCRIPTION)) {
            Property descriptionProperty = new Property<>(PROPERTY_KEY_DATATYPEDESCRIPTION, (IDataTypeDescription) null, global);
            owsGroup.addObject(descriptionProperty);
//            updateDescriptionProperty(datatype);
        }
//
//        if (isGlobalInput() || isFlowInput()) {
//            hasMinMaxOccurs = true;
//
//            if (!owsGroup.hasProperty(PROPERTY_KEY_MINOCCURS)) {
//                owsGroup.addObject(createPortProperty(PROPERTY_KEY_MINOCCURS));
//            }
//
//            if (!owsGroup.hasProperty(PROPERTY_KEY_MAXOCCURS)) {
//                owsGroup.addObject(createPortProperty(PROPERTY_KEY_MAXOCCURS));
//            }
//
//            if (ProcessPortDatatype.COMPLEX.equals(this.datatype)) {
//                hasMaxMb = true;
//
//                if (!owsGroup.hasProperty(PROPERTY_KEY_MAXMB)) {
//                    owsGroup.addObject(createPortProperty(PROPERTY_KEY_MAXMB));
//                }
//            }
//        }
//
//        // remove eventually existing properties if they do not apply to this port
//        if (!hasMinMaxOccurs) {
//            owsGroup.removeProperty(PROPERTY_KEY_MINOCCURS);
//            owsGroup.removeProperty(PROPERTY_KEY_MAXOCCURS);
//        }
//
//        if (!hasMaxMb) {
//            owsGroup.removeProperty(PROPERTY_KEY_MAXMB);
//        }
    }
    
    public IDataTypeDescription getDataTypeDescription() {
        return (IDataTypeDescription) getPropertyValue(PROPERTY_KEY_DATATYPEDESCRIPTION);
    }

    public boolean isGlobal() {
        return global;
    }

    protected void setGlobal(boolean global) {
        this.global = global;

        // TODO this should be done by the sub classes
//        createProperties(getOwsIdentifier());
//        for (Property property : owsGroup.getProperties()) {
//            if (null != property) {
//                property.setEditable(global);
//            }
//        }
    }

    protected void owsPropertiesCreated() {
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

    /**
     * Sets global to true; flowInput depending on the parameter. Note: a global
     * process output is a local flow input.
     *
     * @param isOutput
     */
    protected void setGlobalOutput(boolean isOutput) {
        this.flowInput = isOutput;
        this.global = true;
    }

    /**
     * Sets global to true; flowInput depending on the parameter. Note: a global
     * process input is a local flow output.
     *
     * @param isInput
     */
    protected void setGlobalInput(boolean isInput) {
        this.flowInput = !isInput;
        this.global = true;
    }

    protected void updateDatatypeDescriptionProperty(String componentType) {
        Property property = owsGroup.getPropertyObject(PROPERTY_KEY_DATATYPEDESCRIPTION);
        property.setComponentType(componentType);
    }

//    /**
//     * @param datatype the data to set
//     */
//    public void setDatatype(ProcessPortDatatype datatype) {
//        IDataTypeDescription dataTypeDescription = getDataTypeDescription();
//
//        if (null != dataTypeDescription && !dataTypeDescription.isDescriptionFor(datatype)) {
//            String msg = createIncompatibleDescriptionMessage(datatype, dataTypeDescription);
//            throw new IllegalArgumentException(msg);
//        }
//
//        this.datatype = datatype;
//        createProperties(getOwsIdentifier());
//        updateDescriptionProperty(datatype);
//    }
//    private void updateDescriptionProperty(ProcessPortDatatype datatype) {
//        if (null == datatype) {
//            return;
//        }
//
//        String descPropertyType;
//        switch (datatype) {
//            case COMPLEX:
//                descPropertyType = COMPONENTTYPE_DATATYPEDESCRIPTION_COMPLEX;
//                break;
//            case BOUNDING_BOX:
//                descPropertyType = COMPONENTTYPE_DATATYPEDESCRIPTION_BBOX;
//                break;
//            case LITERAL:
//            default:
//                descPropertyType = COMPONENTTYPE_DATATYPEDESCRIPTION_LITERAL;
//
//        }
//        Property property = owsGroup.getPropertyObject(PROPERTY_KEY_DATATYPEDESCRIPTION);
//        property.setComponentType(descPropertyType);
//    }
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
//        ProcessPort clone = new ProcessPort(datatype, global);
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

    public void cloneInto(ProcessPort clone) {
        super.cloneInto(clone);

        clone.flowInput = flowInput;
        clone.toolTipText = null; // indicate lazy init.

        // creates missing properties etc.
        clone.setGlobal(global);

//        return clone;
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
