/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.richWPS.boundary.handler;

import de.hsos.richwps.mb.richWPS.entity.IInputDescription;
import de.hsos.richwps.mb.richWPS.entity.IInputValue;
import de.hsos.richwps.mb.richWPS.entity.IOutputDescription;
import de.hsos.richwps.mb.richWPS.entity.IOutputValue;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputLiteralDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.values.InputComplexDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.InputLiteralDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.OutputComplexDataValue;
import java.util.HashMap;
import java.util.List;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import net.opengis.wps.x100.ProcessDescriptionType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.n52.wps.client.WPSClientSession;

/**
 *
 * @author dalcacer
 */
public class DescribeRequestHandlerTest {
    
    public DescribeRequestHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of handle method, of class DescribeRequestHandler.
     */
    @Test
    public void testHandle() {
       try {
            System.out.println("testHandle");
            String processid = "org.n52.wps.server.algorithm.test.EchoProcess";

            String wpsurl = "http://richwps.edvsz.hs-osnabrueck.de/wps/WebProcessingService";
            WPSClientSession wps = WPSClientSession.getInstance();
            wps.connect(wpsurl);

            DescribeRequest request = new DescribeRequest();
            request.setEndpoint(wpsurl);
            request.setIdentifier(processid);
            DescribeRequestHandler handler = new DescribeRequestHandler(wps, request);
            handler.handle();

            List inputs = request.getInputs();
            assertNotNull(inputs);
            assertNotEquals(0, inputs.size());
            
            List outputs = request.getOutputs();
            assertNotNull(outputs);
            assertNotEquals(0, outputs.size());
        } catch (Exception e) {
            fail();
        }
    }

    
}
