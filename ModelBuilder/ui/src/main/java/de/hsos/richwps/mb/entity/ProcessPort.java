/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.entity;

import java.io.Serializable;

/**
 *
 * @author dziegenh
 */
public class ProcessPort implements IOwsObject, Serializable {

    private ProcessPortDatatype datatype;
//    private String datatype;
    private String owsIdentifier;
    private String owsTitle;
    private String owsAbstract;

    private String toolTipText = null;

    private boolean flowInput;

    private boolean global;

    public ProcessPort() {
    }

    public ProcessPort(ProcessPortDatatype processPortDatatype, boolean global) {
//        this.datatype = processPortDatatype.name();
        this.datatype = processPortDatatype;
        this.global = global;
    }

    public ProcessPort(ProcessPortDatatype processPortDatatype) {
        this(processPortDatatype, false);
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
//    public String getDatatype() {
//        return datatype;
//    }

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
     * @param isOutput
     */
    public void setGlobalInput(boolean isInput) {
        this.global = true;
        this.flowInput = !isInput;
    }

    /**
     * @param datatype the data to set
     */
    public void setDatatype(ProcessPortDatatype datatype) {
        this.datatype = datatype;
    }
//    public void setDatatype(String datatype) {
//        this.datatype = datatype;
//    }

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

            // length of vars + size of "<html></html>" tags + size of "<b></b>" tags + size of "<hr>" tags + size of "<br>" tags
            int sbCapacity = getOwsTitle().length() + getOwsIdentifier().length() + getOwsAbstract().length() + 13 + 7 + 4 + 8;
            StringBuilder sb = new StringBuilder(sbCapacity);
            sb.append("<html><b>").append(getOwsTitle()).append("</b><br>").append(getOwsIdentifier()).append("<br><hr>").append(getOwsAbstract()).append("</html>");
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
