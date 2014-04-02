/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.semanticProxy.entity;

import java.util.List;

/**
 *
 * @author dziegenh
 */
public interface IProcessEntity {

    String getId();

    String getTitle();

    String getOwsAbstract();

    int getNumInputs();

    int getNumOutputs();

    String getServer();

    @Override
    String toString();

    void addInputPort(ProcessPort port);

    void addOutputPort(ProcessPort port);

    List<ProcessPort> getInputPorts();

    List<ProcessPort> getOutputPorts();

}
