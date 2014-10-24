package de.hsos.richwps.mb.entity;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.properties.Property;

/**
 * A port entitiy can either be global (model-wide) or local (part of a
 * process).
 *
 * @author dziegenh
 */
public class ProcessPort extends OwsObjectWithProperties {

    public static String TOOLTIP_STYLE_INPUT = "";
    public static String TOOLTIP_STYLE_OUTPUT = "";

//    private HashMap<String, Property> properties;
    public static String PROPERTY_KEY_DATATYPEDESCRIPTION = "Datatype description";

    public static String COMPONENTTYPE_DATATYPEDESCRIPTION_COMPLEX = "Datatype description complex";
    public static String COMPONENTTYPE_DATATYPEDESCRIPTION_LITERAL = "Datatype description literal";
    public static String COMPONENTTYPE_DATATYPEDESCRIPTION_BBOX = "Datatype description bbox";

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

        // Property for datatype description
        Property descriptionProperty = new Property<>(PROPERTY_KEY_DATATYPEDESCRIPTION, (IDataTypeDescription) null, global);
        owsGroup.addObject(descriptionProperty);
        updateDescriptionProperty(processPortDatatype);
    }

    public IDataTypeDescription getDataTypeDescription() {
        return (IDataTypeDescription) getPropertyValue(PROPERTY_KEY_DATATYPEDESCRIPTION);
    }

    private String createIncompatibleDescriptionMessage(ProcessPortDatatype datatype, IDataTypeDescription dataTypeDescription) {
        String msg = AppConstants.INCOMPATIBLE_DATATYPE_DESCRIPTION;
        String msgDatatype = (null == datatype) ? "null" : datatype.toString();
        String msgDescription = (null == dataTypeDescription) ? "null" : dataTypeDescription.getClass().getSimpleName();
        return String.format(msg, msgDatatype, msgDescription);
    }

    public void setDataTypeDescription(IDataTypeDescription dataTypeDescription) {
        if (!dataTypeDescription.isDescriptionFor(this.datatype)) {
            String msg = createIncompatibleDescriptionMessage(this.datatype, dataTypeDescription);
            throw new IllegalArgumentException(msg);
        }

        Property property = owsGroup.getPropertyObject(PROPERTY_KEY_DATATYPEDESCRIPTION);
        property.setValue(dataTypeDescription);
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;

        for (Property property : owsGroup.getProperties()) {
            if (null != property) {
                property.setEditable(global);
            }
        }
    }

    /**
     * @return the data
     */
    public ProcessPortDatatype getDatatype() {
        return datatype;
    }

    public void setFlowInput(boolean isInput) {
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

    public void setFlowOutput(boolean isOutput) {
        flowInput = !isOutput;
    }

    /**
     * Sets global to true; flowInput depending on the parameter. Note: a global
     * process output is a local flow input.
     *
     * @param isOutput
     */
    public void setGlobalOutput(boolean isOutput) {
        setGlobal(true);
        this.flowInput = isOutput;
    }

    /**
     * Sets global to true; flowInput depending on the parameter. Note: a global
     * process input is a local flow output.
     *
     * @param isInput
     */
    public void setGlobalInput(boolean isInput) {
        setGlobal(true);
        this.flowInput = !isInput;
    }

    /**
     * @param datatype the data to set
     */
    public void setDatatype(ProcessPortDatatype datatype) {
        IDataTypeDescription dataTypeDescription = getDataTypeDescription();

        if (null != dataTypeDescription && !dataTypeDescription.isDescriptionFor(datatype)) {
            String msg = createIncompatibleDescriptionMessage(datatype, dataTypeDescription);
            throw new IllegalArgumentException(msg);
        }

        this.datatype = datatype;
        updateDescriptionProperty(datatype);
    }

    private void updateDescriptionProperty(ProcessPortDatatype datatype) {
        if (null == datatype) {
            return;
        }

        String descPropertyType;
        switch (datatype) {
            case COMPLEX:
                descPropertyType = COMPONENTTYPE_DATATYPEDESCRIPTION_COMPLEX;
                break;
            case BOUNDING_BOX:
                descPropertyType = COMPONENTTYPE_DATATYPEDESCRIPTION_BBOX;
                break;
            case LITERAL:
            default:
                descPropertyType = COMPONENTTYPE_DATATYPEDESCRIPTION_LITERAL;

        }
        Property property = owsGroup.getPropertyObject(PROPERTY_KEY_DATATYPEDESCRIPTION);
        property.setComponentType(descPropertyType);
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

// TODO update capacity after refactoring!
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
            sb.append("'");

            if (null != getDatatype()) {
                sb.append("[").append(getDatatype().toString().charAt(0)).append("] ");
            }

            sb.append("<b>").append(getOwsTitle()).append("</b><br>").append(getOwsIdentifier()).append("<br><i>").append(getOwsAbstract()).append("</i></div></html>");
            toolTipText = sb.toString();
        }

        return toolTipText;
    }

    public ProcessPort clone() {
        ProcessPort clone = new ProcessPort(datatype, global);
        super.cloneInto(clone);

        clone.flowInput = flowInput;
        clone.toolTipText = null; // indicate lazy init.
//        clone.setDataTypeDescription(getDataTypeDescription());

        return clone;
    }

}
