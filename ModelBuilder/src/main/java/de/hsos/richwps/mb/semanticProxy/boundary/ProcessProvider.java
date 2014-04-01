/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.semanticProxy.boundary;

import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPortDatatype;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author dziegenh
 */
public class ProcessProvider implements IProcessProvider {

    @Override
    public Collection<ProcessEntity> getServerProcesses(String server) {
        // TODO mock
        LinkedList<ProcessEntity> ps = new LinkedList<ProcessEntity>();
        int p = 1 + (int) (4 * Math.random());
        int startid = (int) (100*Math.random());
        for (int i = 0; i < p; i++) {

            ProcessEntity proc = new ProcessEntity(server, ""+(startid+i));

            int numIn = (int) (1+3*Math.random());
            int numOut = (int) (1+3*Math.random());
            ProcessPortDatatype[] ptds = ProcessPortDatatype.values();

            for(int pIn=0; pIn<numIn; pIn++) {
                int pdt = (int) (ptds.length * Math.random());
                ProcessPort inPort = new ProcessPort(ptds[pdt]);
                String pValue = "InPort " + pIn;
                inPort.setOwsAbstract(pValue);
                inPort.setOwsIdentifier(pValue);
                inPort.setOwsTitle(pValue);
                proc.addInputPort(inPort);
            }

            for(int pOut=0; pOut<numOut; pOut++) {
                int pdt = (int) (ptds.length * Math.random());
                ProcessPort outPort = new ProcessPort(ptds[pdt]);
                String pValue = "OutPort " + pOut;
                outPort.setOwsAbstract(pValue);
                outPort.setOwsIdentifier(pValue);
                outPort.setOwsTitle(pValue);
                proc.addOutputPort(outPort);
            }

            ps.add(proc);
        }
        return ps;
    }

    @Override
    public Collection<String> getAllServer() {
        // TODO mocked
        LinkedList<String> l = new LinkedList<String>();
        l.add("Server 1");
        l.add("Server 2");
        return l;
    }

}
