package de.hsos.richwps.mb.entity.oldVersions;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.OwsObjectWithProperties;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import static de.hsos.richwps.mb.entity.ProcessPortDatatype.BOUNDING_BOX;
import static de.hsos.richwps.mb.entity.ProcessPortDatatype.COMPLEX;
import static de.hsos.richwps.mb.entity.ProcessPortDatatype.LITERAL;
import de.hsos.richwps.mb.properties.Property;
import java.util.Objects;

/**
 * A port entitiy can either be global (model-wide) or local (part of a
 * process).
 *
 * @author dziegenh
 */
public class ProcessPort extends OwsObjectWithProperties {

    public static String TOOLTIP_STYLE_INPUT = "";
    public static String TOOLTIP_STYLE_OUTPUT = "";

    public final static String PROPERTY_KEY_DATATYPEDESCRIPTION = "Datatype description";
    public final static String PROPERTY_KEY_MINOCCURS = "Min occurs";
    public final static String PROPERTY_KEY_MAXOCCURS = "Max occurs";
    public final static String PROPERTY_KEY_MAXMB = "Max MB";

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
        updateDescriptionProperty(processPortDatatype);
    }

    public static Property createPortProperty(final String propertyKey) {
        Property created = null;

        switch (propertyKey) {
            case PROPERTY_KEY_MINOCCURS:
                created = new Property<Integer>(PROPERTY_KEY_MINOCCURS, Property.COMPONENT_TYPE_INTEGER, null);
                break;

            case PROPERTY_KEY_MAXOCCURS:
                created = new Property<Integer>(PROPERTY_KEY_MAXOCCURS, Property.COMPONENT_TYPE_INTEGER, null);
                break;

            case PROPERTY_KEY_MAXMB:
                created = new Property<Integer>(PROPERTY_KEY_MAXMB, Property.COMPONENT_TYPE_INTEGER, null);
                break;
        }
        return created;
    }

    @Override
    protected void createProperties(String owsIdentifier) {
        super.createProperties(owsIdentifier);

        boolean hasMinMaxOccurs = false;
        boolean hasMaxMb = false;

        // create missing properties if necessary
        if (!owsGroup.hasProperty(PROPERTY_KEY_DATATYPEDESCRIPTION)) {
            Property descriptionProperty = new Property<>(PROPERTY_KEY_DATATYPEDESCRIPTION, (IDataTypeDescription) null, global);
            owsGroup.addObject(descriptionProperty);
            updateDescriptionProperty(datatype);
        }

        if (isGlobalInput() || isFlowInput()) {
            hasMinMaxOccurs = true;

            if (!owsGroup.hasProperty(PROPERTY_KEY_MINOCCURS)) {
                owsGroup.addObject(createPortProperty(PROPERTY_KEY_MINOCCURS));
            }

            if (!owsGroup.hasProperty(PROPERTY_KEY_MAXOCCURS)) {
                owsGroup.addObject(createPortProperty(PROPERTY_KEY_MAXOCCURS));
            }

            if (ProcessPortDatatype.COMPLEX.equals(this.datatype)) {
                hasMaxMb = true;

                if (!owsGroup.hasProperty(PROPERTY_KEY_MAXMB)) {
                    owsGroup.addObject(createPortProperty(PROPERTY_KEY_MAXMB));
                }
            }
        }

        // remove eventually existing properties if they do not apply to this port
        if (!hasMinMaxOccurs) {
            owsGroup.removeProperty(PROPERTY_KEY_MINOCCURS);
            owsGroup.removeProperty(PROPERTY_KEY_MAXOCCURS);
        }

        if (!hasMaxMb) {
            owsGroup.removeProperty(PROPERTY_KEY_MAXMB);
        }
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

        createProperties(getOwsIdentifier());
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
        createProperties(getOwsIdentifier());
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
        createProperties(getOwsIdentifier());
    }

    /**
     * Sets global to true; flowInput depending on the parameter. Note: a global
     * process output is a local flow input.
     *
     * @param isOutput
     */
    public void setGlobalOutput(boolean isOutput) {
        this.flowInput = isOutput;
        setGlobal(true);
    }

    /**
     * Sets global to true; flowInput depending on the parameter. Note: a global
     * process input is a local flow output.
     *
     * @param isInput
     */
    public void setGlobalInput(boolean isInput) {
        this.flowInput = !isInput;
        setGlobal(true);
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
        createProperties(getOwsIdentifier());
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

            StringBuilder sb = new StringBuilder(1000);
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

    public ProcessPort clone() {
        ProcessPort clone = new ProcessPort(datatype, global);
        super.cloneInto(clone);

        clone.flowInput = flowInput;
        clone.toolTipText = null; // indicate lazy init.

        // creates missing properties etc.
        clone.setGlobal(global);

        return clone;
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

    @Override
    public boolean isTranslatable() {
        return false;
    }

}

