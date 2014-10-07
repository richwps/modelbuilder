package de.hsos.richwps.mb.entity;

import de.hsos.richwps.mb.app.AppConstants;
import java.io.Serializable;

/**
 * A port entitiy can either be global (model-wide) or local (part of a
 * process).
 *
 * @author dziegenh
 */
public class ProcessPort implements IOwsObject, Serializable {

    public static String TOOLTIP_STYLE_INPUT = "";
    public static String TOOLTIP_STYLE_OUTPUT = "";

    private String owsIdentifier = "";
    private String owsTitle = "";
    private String owsAbstract = "";
    private ProcessPortDatatype datatype;
    private IDataTypeDescription dataTypeDescription;

    private String toolTipText = null;

    private boolean flowInput;

    private boolean global;

    public ProcessPort() {
    }

    public ProcessPort(ProcessPortDatatype processPortDatatype, boolean global) {
        this.datatype = processPortDatatype;
        this.global = global;
    }

    public ProcessPort(ProcessPortDatatype processPortDatatype) {
        this(processPortDatatype, false);
    }

    public IDataTypeDescription getDataTypeDescription() {
        return dataTypeDescription;
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

        this.dataTypeDescription = dataTypeDescription;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
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
        this.global = true;
        this.flowInput = isOutput;
    }

    /**
     * Sets global to true; flowInput depending on the parameter. Note: a global
     * process input is a local flow output.
     *
     * @param isInput
     */
    public void setGlobalInput(boolean isInput) {
        this.global = true;
        this.flowInput = !isInput;
    }

    /**
     * @param datatype the data to set
     */
    public void setDatatype(ProcessPortDatatype datatype) {
        if (null != this.dataTypeDescription && !this.dataTypeDescription.isDescriptionFor(datatype)) {
            String msg = createIncompatibleDescriptionMessage(datatype, this.dataTypeDescription);
            throw new IllegalArgumentException(msg);
        }

        this.datatype = datatype;
    }

    /**
     * @return the owsIdentifier
     */
    @Override
    public String getOwsIdentifier() {
        return owsIdentifier;
    }

    /**
     * Sets the identifier and resets the toolTipText.
     *
     * @param owsIdentifier the owsIdentifier to set
     */
    @Override
    public void setOwsIdentifier(String owsIdentifier) {
        this.owsIdentifier = owsIdentifier;
        toolTipText = null;
    }

    /**
     * @return the owsTitle
     */
    @Override
    public String getOwsTitle() {
        return owsTitle;
    }

    /**
     * Sets the title and resets the toolTipText.
     *
     * @param owsTitle the owsTitle to set
     */
    @Override
    public void setOwsTitle(String owsTitle) {
        this.owsTitle = owsTitle;
        toolTipText = null;
    }

    /**
     * @return the owsAbstract
     */
    @Override
    public String getOwsAbstract() {
        return owsAbstract;
    }

    /**
     * Sets the abstract and resets the toolTipText.
     *
     * @param owsAbstract the owsAbstract to set
     */
    @Override
    public void setOwsAbstract(String owsAbstract) {
        this.owsAbstract = owsAbstract;
        toolTipText = null;
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

    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    public ProcessPort clone() {
        ProcessPort clone = new ProcessPort(datatype, global);
        clone.flowInput = flowInput;
        clone.owsAbstract = owsAbstract;
        clone.owsIdentifier = owsIdentifier;
        clone.owsTitle = owsTitle;
        clone.toolTipText = null; // indicate lazy init.

        return clone;
    }

}
