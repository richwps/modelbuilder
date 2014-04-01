/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.semanticProxy.entity;

/**
 *
 * @author dziegenh
 */
public class ProcessPort {

    private ProcessPortDatatype datatype;
    private String owsIdentifier;
    private String owsTitle;
    private String owsAbstract;

    public ProcessPort(ProcessPortDatatype processPortDatatype) {
        this.datatype = processPortDatatype;
    }

    /**
     * @return the data
     */
    public ProcessPortDatatype getDatatype() {
        return datatype;
    }

    /**
     * @param datatype the data to set
     */
    public void setDatatype(ProcessPortDatatype datatype) {
        this.datatype = datatype;
    }

    /**
     * @return the owsIdentifier
     */
    public String getOwsIdentifier() {
        return owsIdentifier;
    }

    /**
     * @param owsIdentifier the owsIdentifier to set
     */
    public void setOwsIdentifier(String owsIdentifier) {
        this.owsIdentifier = owsIdentifier;
    }

    /**
     * @return the owsTitle
     */
    public String getOwsTitle() {
        return owsTitle;
    }

    /**
     * @param owsTitle the owsTitle to set
     */
    public void setOwsTitle(String owsTitle) {
        this.owsTitle = owsTitle;
    }

    /**
     * @return the owsAbstract
     */
    public String getOwsAbstract() {
        return owsAbstract;
    }

    /**
     * @param owsAbstract the owsAbstract to set
     */
    public void setOwsAbstract(String owsAbstract) {
        this.owsAbstract = owsAbstract;
    }

}
