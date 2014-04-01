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
public interface IProcessEntity {

    String getId();

    int getNumInputs();

    int getNumOutputs();

    String getServer();

    String toString();

    void addInputPort(ProcessPort port);

    void addOutputPort(ProcessPort port);

}
