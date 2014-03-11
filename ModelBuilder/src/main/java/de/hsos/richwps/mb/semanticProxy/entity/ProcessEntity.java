/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.semanticProxy.entity;

/**
 * There is a high propability that this model will be replaced after the
 * ModelBuilder "mock version" is done :) .
 *
 * @author dziegenh
 */
public class ProcessEntity implements IProcessEntity   {

    private String server;
    private String id;

    private int numInputs;
    private int numOutputs;

    public ProcessEntity(String server, String id) {
        this.server = server;
        this.id = id;

        this.numInputs = -1;
        this.numOutputs = -1;
    }
    
    @Override
    public String getServer() { return server; }
    @Override
    public String getId() { return id; }
    
    @Override
    public int getNumInputs() {
        if(0 > numInputs)
            numInputs = 1 + (int) (3 * Math.random());
        
        return numInputs;
    }

    @Override
    public int getNumOutputs() {
        if(0 > numOutputs)
            numOutputs = 1 + (int) (3 * Math.random());

        return numOutputs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(server.length() + id.length() + 1);
        return sb.append(server).append('.').append(id).toString();
    }

}
