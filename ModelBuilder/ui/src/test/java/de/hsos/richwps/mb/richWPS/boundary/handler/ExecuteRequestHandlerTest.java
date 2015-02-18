package de.hsos.richwps.mb.richWPS.boundary.handler;

import de.hsos.richwps.mb.richWPS.entity.IInputDescription;
import de.hsos.richwps.mb.richWPS.entity.IInputValue;
import de.hsos.richwps.mb.richWPS.entity.IOutputDescription;
import de.hsos.richwps.mb.richWPS.entity.IOutputValue;
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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.n52.wps.client.WPSClientSession;

/**
 *
 * @author dalcacer
 */
public class ExecuteRequestHandlerTest {

    

    public ExecuteRequestHandlerTest() {
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

 
    public void testHandle() {
        try {
            System.out.println("testHandle");
            String processid = "org.n52.wps.server.algorithm.test.EchoProcess";

            String wpsurl = "http://richwps.edvsz.hs-osnabrueck.de/wps/WebProcessingService";
            WPSClientSession wps = WPSClientSession.getInstance();
            wps.connect(wpsurl);

            ExecuteRequest request = new ExecuteRequest();

            request.setEndpoint(wpsurl);
            request.setIdentifier(processid);
            ExecuteRequestHandler handler = new ExecuteRequestHandler(wps, request);
            handler.handle();

            List<IInputDescription> inputs = request.getInputs();

            assertEquals(2, inputs.size());
            InputComplexDataDescription geomSpec;
            InputLiteralDataDescription literalSpec;
            if (inputs.get(0) instanceof InputComplexDataDescription) {
                geomSpec = (InputComplexDataDescription) inputs.get(0);
                literalSpec = (InputLiteralDataDescription) inputs.get(1);
            } else {
                geomSpec = (InputComplexDataDescription) inputs.get(1);
                literalSpec = (InputLiteralDataDescription) inputs.get(0);
            }

            HashMap<String, IInputValue> ins = new HashMap<>();
            InputLiteralDataValue arg1 = new InputLiteralDataValue(literalSpec, "Hello World.");
            InputComplexDataValue arg2 = new InputComplexDataValue(geomSpec);
            arg2.setAsReference(true);
            arg2.setURL("http://map.ices.dk/geoserver/sf/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=sf:roads&maxFeatures=50");
            arg2.setMimeType("text/xml");
            ins.put("literalInput", arg1);
            ins.put("complexInput", arg2);
            request.setInputValues(ins);

            HashMap<String, IOutputDescription> outs = new HashMap();
            List<IOutputValue> outputs = request.getOutputs();
            OutputComplexDataDescription outspec;
            if (outputs.get(0) instanceof OutputComplexDataDescription) {
                outspec = (OutputComplexDataDescription) outputs.get(0);
            } else {
                outspec = (OutputComplexDataDescription) outputs.get(1);
            }

            OutputComplexDataValue outarg = new OutputComplexDataValue(outspec);
            outarg.setAsReference(true);
            outarg.setMimetype("text/html");
            outs.put("result", outarg);

            handler.handle();

            HashMap<String, Object> theResults = request.getResults();
            assertNotNull(theResults);
        } catch (Exception e) {
            fail();
        }
    }

}
