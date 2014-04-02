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
import java.util.LinkedList;
import java.util.List;

/**
 * There is a high propability that this model will be replaced after the
 * ModelBuilder "mock version" is done :) .
 *
 * @author dziegenh
 */
public class ProcessEntity implements IProcessEntity, Transferable   {

    private String title;
    private String owsAbstract;
    private String server;
    private String id;

    private LinkedList<ProcessPort> inputPorts;
    private LinkedList<ProcessPort> outputPorts;

//    private int numInputs;
//    private int numOutputs;

    public ProcessEntity(String server, String id) {
        this.server = server;
        this.id = id;

        this.inputPorts = new LinkedList<ProcessPort>();
        this.outputPorts = new LinkedList<ProcessPort>();

//        this.numInputs = -1;
//        this.numOutputs = -1;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOwsAbstract(String owsAbstract) {
        this.owsAbstract = owsAbstract;
    }

    @Override
    public String getServer() { return server; }
    @Override
    public String getId() { return id; }
    
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
        StringBuilder sb = new StringBuilder(server.length() + id.length() + 1);
//        StringBuilder sb = new StringBuilder();
        return sb.append(server).append('.').append(id).toString();
    }

    public void addInputPort(ProcessPort port) {
        inputPorts.add(port);
    }

    public void addOutputPort(ProcessPort port) {
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



}
