/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.entity;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * There is a high propability that this model will be replaced after the
 * ModelBuilder "mock version" is done :) .
 *
 * @author dziegenh
 */
public class ProcessEntity implements IOwsObject, Transferable, Serializable {

    private String owsTitle;
    private String owsAbstract;
    private String server;
    private String owsIdentifier;

    private LinkedList<ProcessPort> inputPorts;
    private LinkedList<ProcessPort> outputPorts;
    private String toolTipText;

    public ProcessEntity() {
        this("", "");
    }

    public ProcessEntity(String server, String owsIdentifier) {
        this.server = server;
        this.owsIdentifier = owsIdentifier;

        this.inputPorts = new LinkedList<>();
        this.outputPorts = new LinkedList<>();
    }

    public void setInputPorts(LinkedList<ProcessPort> ports) {
        this.inputPorts = ports;
    }

    public void setOutputPorts(LinkedList<ProcessPort> ports) {
        this.outputPorts = ports;
    }

    /**
     * Sets the title and resets the toolTipText.
     * @param owsTitle
     */
    @Override
    public void setOwsTitle(String owsTitle) {
        this.owsTitle = owsTitle;
        toolTipText = null;
    }

    /**
     * Sets the identifier and resets the toolTipText.
     * @param owsIdentifier
     */
    @Override
    public void setOwsIdentifier(String owsIdentifier) {
        this.owsIdentifier = owsIdentifier;
        toolTipText = null;
    }

    /**
     * Sets the abstract and resets the toolTipText.
     * @param owsAbstract 
     */
    @Override
    public void setOwsAbstract(String owsAbstract) {
        this.owsAbstract = owsAbstract;
        toolTipText = null;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getServer() { 
        return server;
    }

    @Override
    public String getOwsIdentifier() {
        return owsIdentifier;
    }
    
    public int getNumInputs() {
        return inputPorts.size();
    }

    public int getNumOutputs() {
        return outputPorts.size();
    }

    @Override
    public String toString() {
        return getOwsTitle();
    }

    public void addInputPort(ProcessPort port) {
        port.setFlowInput(true);
        inputPorts.add(port);
    }

    public void addOutputPort(ProcessPort port) {
        port.setFlowOutput(true);
        outputPorts.add(port);
    }

    public List<ProcessPort> getInputPorts() {
        return inputPorts;
    }

    public List<ProcessPort> getOutputPorts() {
        return outputPorts;
    }

    public DataFlavor[] getTransferDataFlavors() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getOwsTitle() {
        return owsTitle;
    }

    @Override
    public String getOwsAbstract() {
        return owsAbstract;
    }

    public String getToolTipText() {
        if(null == toolTipText) {
            // length of vars + size of "<html></html>" tags + size of "<b></b>" tags + size of "<hr>" tags + size of "<br>" tags
            int sbCapacity = getOwsTitle().length() + getOwsIdentifier().length() + getOwsAbstract().length() + 13 + 7 + 4 + 8;
            StringBuilder sb = new StringBuilder(sbCapacity);
            sb.append("<html><b>").append(getOwsTitle()).append("</b><br>").append(getOwsIdentifier()).append("<br><hr>").append(getOwsAbstract()).append("</html>");
            toolTipText = sb.toString();
        }

        return toolTipText;
    }

    /**
     * ProcessEntities are equal if their server and identifier match.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if(null == obj || !(obj instanceof ProcessEntity)) {
            return false;
        }

        ProcessEntity other = (ProcessEntity) obj;
        return other.getServer().equals(this.getServer()) && other.getOwsIdentifier().equals(this.getOwsIdentifier());
    }

    public ProcessEntity clone() {
        ProcessEntity clone = new ProcessEntity(server, owsIdentifier);
        clone.owsAbstract = owsAbstract;
        clone.owsTitle = owsTitle;
        clone.toolTipText = null; // indicate lazy initialisation

        for(ProcessPort inPort : inputPorts) {
            clone.addInputPort(inPort.clone());
        }

        for(ProcessPort outPort : outputPorts) {
            clone.addOutputPort(outPort.clone());
        }

        return clone;
    }

    /**
     * OWS Processes are equal if server and identifier are equal.
     * @param process
     * @return
     */
    public boolean owsEquals(Object process) {
        if(null == process)
            return false;

        if(!(process instanceof ProcessEntity))
            return false;

        ProcessEntity other = (ProcessEntity) process;

        boolean serverEqual = getServer().equals(other.getServer());
        boolean identifierEqual = getOwsIdentifier().equals(other.getOwsIdentifier());
        
        return serverEqual && identifierEqual;
    }

}
