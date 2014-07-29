/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.semanticProxy.entity;

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
public class ProcessEntity implements IProcessEntity, Transferable, Serializable {

    private String title;
    private String owsAbstract;
    private String server;
    private String identifier;

    private LinkedList<ProcessPort> inputPorts;
    private LinkedList<ProcessPort> outputPorts;
    private String toolTipText;

    public ProcessEntity() {
        this("", "");
    }

    public ProcessEntity(String server, String identifier) {
        this.server = server;
        this.identifier = identifier;

        this.inputPorts = new LinkedList<ProcessPort>();
        this.outputPorts = new LinkedList<ProcessPort>();
    }

    public void setInputPorts(LinkedList<ProcessPort> ports) {
        this.inputPorts = ports;
    }

    public void setOutputPorts(LinkedList<ProcessPort> ports) {
        this.outputPorts = ports;
    }

    /**
     * Sets the title and resets the toolTipText.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
        toolTipText = null;
    }

    /**
     * Sets the identifier and resets the toolTipText.
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        toolTipText = null;
    }

    /**
     * Sets the abstract and resets the toolTipText.
     * @param owsAbstract 
     */
    public void setOwsAbstract(String owsAbstract) {
        this.owsAbstract = owsAbstract;
        toolTipText = null;
    }

    public void setServer(String server) {
        this.server = server;
    }

    @Override
    public String getServer() { return server; }
    @Override
    public String getIdentifier() { return identifier; }
    
    @Override
    public int getNumInputs() {
        return inputPorts.size();
    }

    @Override
    public int getNumOutputs() {
        return outputPorts.size();
    }

    @Override
    public String toString() {
        return getTitle();
//        StringBuilder sb = new StringBuilder(server.length() + identifier.length() + 1);
//        return sb.append(server).append('.').append(identifier).toString();
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

    public String getTitle() {
        return title;
    }

    public String getOwsAbstract() {
        return owsAbstract;
    }

    public String getToolTipText() {
        if(null == toolTipText) {
            // length of vars + size of "<html></html>" tags + size of "<b></b>" tags + size of "<hr>" tags + size of "<br>" tags
            int sbCapacity = getTitle().length() + getIdentifier().length() + getOwsAbstract().length() + 13 + 7 + 4 + 8;
            StringBuilder sb = new StringBuilder(sbCapacity);
            sb.append("<html><b>").append(getTitle()).append("</b><br>").append(getIdentifier()).append("<br><hr>").append(getOwsAbstract()).append("</html>");
            toolTipText = sb.toString();
        }

        return toolTipText;
    }


}
