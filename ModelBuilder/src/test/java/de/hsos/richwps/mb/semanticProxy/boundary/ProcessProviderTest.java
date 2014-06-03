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

    private String url = "http://localhost:4567/semanticproxy/resources";
    private ProcessProvider instance;


    public ProcessProviderTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        instance = new ProcessProvider(url);
    }

    @Override
    protected void tearDown() throws Exception {
        instance = null;
        super.tearDown();
    }

    /**
     * Test of getServerProcesses method, of class ProcessProvider.
     */
    public void testGetServerProcesses() {
        System.out.println("getServerProcesses");
        String server = "";
        Collection<ProcessEntity> result = instance.getServerProcesses(server);
        assertFalse(result.isEmpty());
    }

    /**
     * Test of getAllServer method, of class ProcessProvider.
     */
    public void testGetAllServer() {
        System.out.println("getAllServer");
        Collection<String> result = instance.getAllServer();
        assertFalse(result.isEmpty());
    }

}
