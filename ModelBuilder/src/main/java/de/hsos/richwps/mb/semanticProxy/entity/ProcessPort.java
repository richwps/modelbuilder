/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.semanticProxy.entity;

import java.io.Serializable;

/**
 *
 * @author dziegenh
 */
public class ProcessPort implements Serializable {

//    private ProcessPortDatatype datatype;
    private String datatype;
    private String owsIdentifier;
    private String owsTitle;
    private String owsAbstract;

    private String toolTipText = null;

    private boolean input;

    public ProcessPort() {
    }

    public ProcessPort(ProcessPortDatatype processPortDatatype) {
        this.datatype = processPortDatatype.name();
    }

    /**
     * @return the data
     */
//    public ProcessPortDatatype getDatatype() {
//        return datatype;
//    }
    public String getDatatype() {
        return datatype;
    }

    public void setInput(boolean isInput) {
        input = isInput;
    }

    public boolean isInput() {
        return input;
    }

    public boolean isOutput() {
        return !input;
    }

    public void setOutput(boolean isOutput) {
        input = !isOutput;
    }

    /**
     * @param datatype the data to set
     */
//    public void setDatatype(ProcessPortDatatype datatype) {
//        this.datatype = datatype;
//    }
    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    /**
     * @return the owsIdentifier
     */
    public String getOwsIdentifier() {
        return owsIdentifier;
    }

    /**
     * Sets the identifier and resets the toolTipText.
     * @param owsIdentifier the owsIdentifier to set
     */
    public void setOwsIdentifier(String owsIdentifier) {
        this.owsIdentifier = owsIdentifier;
        toolTipText = null;
    }

    /**
     * @return the owsTitle
     */
    public String getOwsTitle() {
        return owsTitle;
    }

    /**
     * Sets the title and resets the toolTipText.
     * @param owsTitle the owsTitle to set
     */
    public void setOwsTitle(String owsTitle) {
        this.owsTitle = owsTitle;
        toolTipText = null;
    }

    /**
     * @return the owsAbstract
     */
    public String getOwsAbstract() {
        return owsAbstract;
    }

    /**
     * Sets the abstract and resets the toolTipText.
     * @param owsAbstract the owsAbstract to set
     */
    public void setOwsAbstract(String owsAbstract) {
        this.owsAbstract = owsAbstract;
        toolTipText = null;
    }

    @Override
    public String toString() {
        // TODO mocked!
        if(null != getDatatype())
            return getDatatype().substring(0,1).toUpperCase();
        else
          return getOwsTitle().length() < 3 ? getOwsTitle() : getOwsTitle().substring(0, 3) + "...";
    }

    public String getToolTipText() {
        if(null == toolTipText) {
            if(null == getOwsTitle() || null == getOwsAbstract() || null == getOwsIdentifier()) {
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

}
