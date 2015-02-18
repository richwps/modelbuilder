package de.hsos.richwps.mb.richWPS.boundary.handler;

import de.hsos.richwps.mb.richWPS.entity.IInputDescription;
import de.hsos.richwps.mb.richWPS.entity.IInputValue;
import de.hsos.richwps.mb.richWPS.entity.IOutputDescription;
import de.hsos.richwps.mb.richWPS.entity.IOutputValue;
import de.hsos.richwps.mb.richWPS.entity.impl.GetProcessesRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputLiteralDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.values.InputComplexDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.InputLiteralDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.OutputComplexDataValue;
import it.geosolutions.imageioimpl.plugins.tiff.TIFFFaxCompressor;
import java.util.HashMap;
import java.util.List;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
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
public class GetProcessesRequestHandlerTest {

    public GetProcessesRequestHandlerTest() {
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
     * Test of handle method, of class GetProcessesRequestHandler.
     */
    @Test
    public void testHandle() {
        try {
            System.out.println("testHandle");
            String wpsurl = "http://richwps.edvsz.hs-osnabrueck.de/wps/WebProcessingService";
            WPSClientSession wps = WPSClientSession.getInstance();
            wps.connect(wpsurl);

            GetProcessesRequest request = new GetProcessesRequest(wpsurl);

            GetProcessesRequestHandler handler = new GetProcessesRequestHandler(wps, request);
            handler.handle();
            List<String> results = request.getProcesses();
            assertNotNull(results);
            assertNotEquals(0, results.size());
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Test of handle method, of class GetProcessesRequestHandler.
     */
    @Test
    public void testInvalidHandle() {
        try {
            System.out.println("testInvalidHandle");
            String wpsurl = "http://richwps.edvsz.hs-osnabrueck.de/wps/aaaababbababa";
            WPSClientSession wps = WPSClientSession.getInstance();
            wps.connect(wpsurl);

            GetProcessesRequest request = new GetProcessesRequest(wpsurl);

            GetProcessesRequestHandler handler = new GetProcessesRequestHandler(wps, request);
            handler.handle();
        } catch (Exception e) {
            assertNotNull(e);
        }
    }
}
