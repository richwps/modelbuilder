/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.semanticProxy.boundary;

import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import java.util.Collection;
import junit.framework.TestCase;

/**
 *
 * @author dziegenh
 */
public class ProcessProviderTest extends TestCase {

    public ProcessProviderTest(String testName) {
        super(testName);
    }

    /**
     * Test of getServerProcesses method, of class ProcessProvider.
     */
    public void testGetServerProcesses() {
        System.out.println("getServerProcesses");
        String server = "";
        ProcessProvider instance = new ProcessProvider();
        Collection<ProcessEntity> result = instance.getServerProcesses(server);
        assertFalse(result.isEmpty());
    }

    /**
     * Test of getAllServer method, of class ProcessProvider.
     */
    public void testGetAllServer() {
        System.out.println("getAllServer");
        ProcessProvider instance = new ProcessProvider();
        Collection<String> result = instance.getAllServer();
        assertFalse(result.isEmpty());
    }

}
