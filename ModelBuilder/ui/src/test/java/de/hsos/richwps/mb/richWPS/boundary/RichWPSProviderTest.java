package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.richWPS.entity.execute.ExecuteRequestDTO;
import de.hsos.richwps.mb.richWPS.entity.*;
import de.hsos.richwps.mb.richWPS.entity.execute.InputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.execute.InputLiteralDataArgument;
import de.hsos.richwps.mb.richWPS.entity.execute.OutputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.specifier.InputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.specifier.InputLiteralDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.specifier.OutputComplexDataSpecifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author dalcacer
 */
public class RichWPSProviderTest extends TestCase {

    private String wpsurl = "http://geoprocessing.demo.52north.org:8080/wps/WebProcessingService";

    public RichWPSProviderTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of connect method, of class RichWPSProvider.
     */
    public void testConnect_String() {
        System.out.println("connect");
        RichWPSProvider instance = new RichWPSProvider();
        try {
            instance.connect(wpsurl);
        } catch (Exception e) {
            fail("Unable to connect.");
        }

    }

    /**
     * Test of getAvailableProcesses method, of class RichWPSProvider.
     */
    public void testGetAvailableProcesses() {
        System.out.println("getAvailableProcesses");
        RichWPSProvider instance = new RichWPSProvider();
        instance.connect(wpsurl);
        /*List<String> expResult = new ArrayList<String>();
        expResult.add("org.n52.wps.server.algorithm.test.MultiReferenceInputAlgorithm");
        expResult.add("org.n52.wps.server.algorithm.JTSConvexHullAlgorithm");
        expResult.add("org.n52.wps.server.algorithm.SimpleBufferAlgorithm");
        expResult.add("org.n52.wps.server.algorithm.test.MultipleComplexInAndOutputsDummyTestClass");
        expResult.add("org.n52.wps.server.algorithm.coordinatetransform.CoordinateTransformAlgorithm");
        expResult.add("org.n52.wps.server.algorithm.raster.AddRasterValues");
        expResult.add("org.n52.wps.server.algorithm.simplify.DouglasPeuckerAlgorithm");
        expResult.add("org.n52.wps.server.algorithm.spatialquery.IntersectsAlgorithm");
        expResult.add("org.n52.wps.server.algorithm.test.DummyTestClass");
        expResult.add("org.n52.wps.server.algorithm.intersection.IntersectionAlgorithm");
        expResult.add("org.n52.wps.server.algorithm.spatialquery.TouchesAlgorithm");
        expResult.add("org.n52.wps.server.algorithm.test.MultiReferenceBinaryInputAlgorithm");
        expResult.add("org.n52.wps.server.algorithm.test.LongRunningDummyTestClass");
        expResult.add("org.n52.wps.server.algorithgeuelPriceProcess");
        expResult.add("org.envirocar.wps.TrackToCSVProcess");
        expResult.add("org.envirocar.wps.HarvestAllTracksProcess");*/
        List<String> result = instance.getAvailableProcesses(wpsurl);
        assertNotSame(new ArrayList<String>(), result); //not empty!
    }

    /**
     * Test of describeProcess method, of class RichWPSProvider.
     */
    public void testDescribeProcess() {
        System.out.println("describeProcess");
        String processid = "org.n52.wps.server.algorithm.test.DummyTestClass";

        RichWPSProvider instance = new RichWPSProvider();
        instance.connect(wpsurl);

        ExecuteRequestDTO dto = new ExecuteRequestDTO();
        dto.setEndpoint(wpsurl);
        dto.setProcessid(processid);
        ExecuteRequestDTO result = instance.describeProcess(dto);
        List<IInputSpecifier> inputs = result.getInputSpecifier();
        assertEquals(2, inputs.size()); //3 with BBOX suport
        assertEquals("ComplexInputData", ((IInputSpecifier) inputs.get(0)).getIdentifier());
        assertEquals("LiteralInputData", ((IInputSpecifier) inputs.get(1)).getIdentifier());
        //assertEquals("BBOxInputData", ((IInputSpecifier) inputs.get(2)).getIdentifier());
        
        List<IOutputSpecifier> outputs = result.getOutputSepcifier();
        assertEquals(2, outputs.size()); //3 with BBOX suport
        assertEquals("ComplexOutputData", ((IOutputSpecifier)outputs.get(0)).getIdentifier());
        assertEquals("LiteralOutputData", ((IOutputSpecifier)outputs.get(1)).getIdentifier());
        //assertEquals("BBOXOutputData", ((IOutputSpecifier)outputs.get(2)).getIdentifier());
        
    }

    /**
     * Test of describeProcess method, of class RichWPSProvider.
     */
    public void testSimpleBuffer() {
        System.out.println("simpleBuffer");
        String processid = "org.n52.wps.server.algorithm.SimpleBufferAlgorithm";

        RichWPSProvider instance = new RichWPSProvider();
        instance.connect(wpsurl);

        ExecuteRequestDTO dto = new ExecuteRequestDTO();
        dto.setEndpoint(wpsurl);
        dto.setProcessid(processid);

        dto = instance.describeProcess(dto);
        List<IInputSpecifier> inputs = dto.getInputSpecifier();
        System.out.println(inputs);
        assertEquals(2, inputs.size());
        InputLiteralDataSpecifier widthSpec = (InputLiteralDataSpecifier) inputs.get(0);
        InputComplexDataSpecifier geomSpec = (InputComplexDataSpecifier) inputs.get(1);

        HashMap<String, IInputArgument> ins = new HashMap<>();
        InputLiteralDataArgument arg1 = new InputLiteralDataArgument(widthSpec, "0.5");
        InputComplexDataArgument arg2 = new InputComplexDataArgument(geomSpec);
        arg2.setAsReference(true);
        arg2.setURL("http://map.ices.dk/geoserver/sf/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=sf:roads&maxFeatures=50");
        arg2.setMimeType("text/xml");
        ins.put("width", arg1);
        ins.put("data", arg2);
        dto.setInputArguments(ins);

        HashMap<String, IOutputArgument> outs = new HashMap();
        List<IOutputSpecifier> outputs = dto.getOutputSepcifier();
        OutputComplexDataSpecifier outspec = (OutputComplexDataSpecifier) outputs.get(0);
        OutputComplexDataArgument outarg = new OutputComplexDataArgument(outspec);
        outarg.setAsReference(true);
        outarg.setMimetype("text/html");
        outs.put("result", outarg);

        dto = instance.executeProcess(dto);

        HashMap<String, Object> theResults = dto.getResults();
        assertNotNull(theResults);
    }

}