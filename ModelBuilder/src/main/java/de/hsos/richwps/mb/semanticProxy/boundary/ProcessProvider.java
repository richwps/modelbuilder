/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.semanticProxy.boundary;

import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
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
        for (int i = 0; i < p; i++) {
            ps.add(new ProcessEntity(server, (new Integer(i)).toString()));
        }
        return ps;
    }

    @Override
    public Collection<String> getAllServer() {
        LinkedList<String> l = new LinkedList<String>();
        l.add("m.o.c.k.");
        return l;
    }

}
