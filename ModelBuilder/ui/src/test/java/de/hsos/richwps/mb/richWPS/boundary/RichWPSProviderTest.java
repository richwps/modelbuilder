package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.richWPS.entity.impl.RequestExecute;
import de.hsos.richwps.mb.richWPS.entity.*;
import de.hsos.richwps.mb.richWPS.entity.impl.RequestDeploy;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputLiteralDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputLiteralDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputLiteralDataSpecifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public void testConnect() {
        System.out.println("testConnect");
        RichWPSProvider instance = new RichWPSProvider();
        try {
            instance.connect(wpsurl);
        } catch (Exception e) {
            fail("Unable to connect.");
        }
    }
    
    /**
     * Test of connect method, of class RichWPSProvider.
     */
    public void testConnectException() {
        System.out.println("testConnect");
        RichWPSProvider instance = new RichWPSProvider();
        try {
            instance.connect("http://no");
        } catch (Exception e) {
            assertNotNull(e);
            return;
        }
        fail("Expected exception was not raised.");
    }
    
    /**
     * Test of getAvailableProcesses method, of class RichWPSProvider.
     */
    public void testGetAvailableProcesses() {
        System.out.println("getAvailableProcesses");
        RichWPSProvider instance = new RichWPSProvider();
        try {
            instance.connect(wpsurl);
        } catch (Exception ex) {
            Logger.getLogger(RichWPSProviderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            instance.connect(wpsurl);
        } catch (Exception ex) {
            Logger.getLogger(RichWPSProviderTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        RequestExecute dto = new RequestExecute();
        dto.setEndpoint(wpsurl);
        dto.setIdentifier(processid);
        instance.describeProcess(dto);
        List<IInputSpecifier> inputs = dto.getInputs();
        assertEquals(2, inputs.size()); //3 with BBOX suport
        assertEquals("ComplexInputData", ((IInputSpecifier) inputs.get(0)).getIdentifier());
        assertEquals("LiteralInputData", ((IInputSpecifier) inputs.get(1)).getIdentifier());
        //assertEquals("BBOxInputData", ((IInputSpecifier) inputs.get(2)).getIdentifier());

        List<IOutputSpecifier> outputs = dto.getOutputs();
        assertEquals(2, outputs.size()); //3 with BBOX suport
        assertEquals("ComplexOutputData", ((IOutputSpecifier) outputs.get(0)).getIdentifier());
        assertEquals("LiteralOutputData", ((IOutputSpecifier) outputs.get(1)).getIdentifier());
        //assertEquals("BBOXOutputData", ((IOutputSpecifier)outputs.get(2)).getIdentifier());   
    }

    public void testEchoProcess() {
        System.out.println("testEchoProcess");
        String processid = "org.n52.wps.server.algorithm.test.EchoProcess";

        RichWPSProvider instance = new RichWPSProvider();
        try {
            instance.connect(wpsurl);
        } catch (Exception ex) {
            Logger.getLogger(RichWPSProviderTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        RequestExecute dto = new RequestExecute();
        dto.setEndpoint(wpsurl);
        dto.setIdentifier(processid);

        instance.describeProcess(dto);
        List<IInputSpecifier> inputs = dto.getInputs();
        
        assertEquals(2, inputs.size());
        InputComplexDataSpecifier geomSpec = (InputComplexDataSpecifier) inputs.get(0);
        InputLiteralDataSpecifier literalSpec = (InputLiteralDataSpecifier) inputs.get(1);

        HashMap<String, IInputArgument> ins = new HashMap<>();
        InputLiteralDataArgument arg1 = new InputLiteralDataArgument(literalSpec, "Hello World.");
        InputComplexDataArgument arg2 = new InputComplexDataArgument(geomSpec);
        arg2.setAsReference(true);
        arg2.setURL("http://map.ices.dk/geoserver/sf/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=sf:roads&maxFeatures=50");
        arg2.setMimeType("text/xml");
        ins.put("literalInput", arg1);
        ins.put("complexInput", arg2);
        dto.setInputArguments(ins);

        HashMap<String, IOutputArgument> outs = new HashMap();
        List<IOutputSpecifier> outputs = dto.getOutputs();
        OutputComplexDataSpecifier outspec = (OutputComplexDataSpecifier) outputs.get(0);
        OutputComplexDataArgument outarg = new OutputComplexDataArgument(outspec);
        outarg.setAsReference(true);
        outarg.setMimetype("text/html");
        outs.put("result", outarg);

        instance.executeProcess(dto);

        HashMap<String, Object> theResults = dto.getResults();
        assertNotNull(theResults);
    }

    public void testEchoProcessException() {
        System.out.println("testEchoProcessException");
        String processid = "org.n52.wps.server.algorithm.test.EchoProcess";

        RichWPSProvider instance = new RichWPSProvider();
        try {
            instance.connect(wpsurl);
        } catch (Exception ex) {
            Logger.getLogger(RichWPSProviderTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        RequestExecute dto = new RequestExecute();
        dto.setEndpoint(wpsurl);
        dto.setIdentifier(processid);

        instance.describeProcess(dto);
        List<IInputSpecifier> inputs = dto.getInputs();
        
        assertEquals(2, inputs.size());
        InputComplexDataSpecifier geomSpec = (InputComplexDataSpecifier) inputs.get(0);
        InputLiteralDataSpecifier literalSpec = (InputLiteralDataSpecifier) inputs.get(1);

        HashMap<String, IInputArgument> ins = new HashMap<>();
        InputLiteralDataArgument arg1 = new InputLiteralDataArgument(literalSpec, "Hello World.");
        InputComplexDataArgument arg2 = new InputComplexDataArgument(geomSpec);
        arg2.setAsReference(true);
        arg2.setURL("http://no.domain");
        arg2.setMimeType("text/xml");
        ins.put("literalInput", arg1);
        ins.put("complexInput", arg2);
        dto.setInputArguments(ins);

        HashMap<String, IOutputArgument> outs = new HashMap();
        List<IOutputSpecifier> outputs = dto.getOutputs();
        OutputComplexDataSpecifier outspec = (OutputComplexDataSpecifier) outputs.get(0);
        OutputComplexDataArgument outarg = new OutputComplexDataArgument(outspec);
        outarg.setAsReference(true);
        outarg.setMimetype("text/html");
        outs.put("result", outarg);

        instance.executeProcess(dto);

        assertTrue(dto.isException());
        
        assertNotNull(dto.getException());
    }
    
     private IInputSpecifier createComplexDataInput() {
        InputComplexDataSpecifier specifier;
        specifier = new InputComplexDataSpecifier();
        specifier.setIdentifier("aabb input.");
        specifier.setTitle("aabb input.");
        specifier.setMinOccur(0);
        specifier.setMaxOccur(1);
        specifier.setAbstract("aabb's abstract.");
        
        List<String> atype = new ArrayList<>();
        atype.add("application/xml");   // mimetype
        atype.add("");  // schema
        atype.add("");  // encoding
        List<String> anothertype = new ArrayList<>();
        anothertype.add("text/xml");   // mimetype
        anothertype.add("");  // schema
        anothertype.add("");  // encoding
        
        List<List> types = new ArrayList<>();
        types.add(atype);
        types.add(anothertype);
        
        specifier.setTypes(types);
        specifier.setDefaulttype(atype);
        specifier.setMaximumMegabytes(5);
        return specifier;
    }

    private IInputSpecifier createLiteralDataInput() {
        InputLiteralDataSpecifier specifier = new InputLiteralDataSpecifier();
        specifier.setIdentifier("aabb");
        specifier.setTitle("aabb");
        specifier.setAbstract("aabb's abstract");
        specifier.setMinOccur(0);
        specifier.setMaxOccur(1);
        specifier.setType(("xs:string"));
        specifier.setDefaultvalue("aab");
        return specifier;
    }

    private IOutputSpecifier createComplexDataOutput() {
        OutputComplexDataSpecifier specifier = new OutputComplexDataSpecifier();
        specifier.setIdentifier("aabb");
        specifier.setTitle("aabb");
        specifier.setTheabstract("aabb's abstract");
        
        List<String> atype = new ArrayList<>();
        atype.add("application/xml");   // mimetype
        atype.add("");  // schema
        atype.add("");  // encoding
        List<String> anothertype = new ArrayList<>();
        anothertype.add("text/xml");   // mimetype
        anothertype.add("");  // schema
        anothertype.add("");  // encoding
        
        List<List> types = new ArrayList<>();
        types.add(atype);
        types.add(anothertype);
        
        specifier.setTypes(types);
        specifier.setDefaulttype(atype);
        return specifier;
    }

    private IOutputSpecifier createLiteralDataOutput() {
        OutputLiteralDataSpecifier specifier = new OutputLiteralDataSpecifier();
        specifier.setIdentifier("aabb");
        specifier.setTitle("aabb");
        specifier.setAbstract("aabb's abstract");
        specifier.setType("xs:string");
        return specifier;
    }

     public void testDeploy() {
        System.out.println("testDeploy");
        

        RichWPSProvider instance = new RichWPSProvider();
        try {
            instance.connect(wpsurl, wpsurl);
        } catch (Exception ex) {
            Logger.getLogger(RichWPSProviderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("testCreation");
        RequestDeploy dto = new RequestDeploy("localhost", "test", "test", "1.0", "ROLA");
        dto.addInput(this.createComplexDataInput());
        dto.addInput(this.createLiteralDataInput());
        dto.addOutput(this.createComplexDataOutput());
        dto.addOutput(this.createLiteralDataOutput());
        dto.setExecutionUnit("Execunit.");
        
        instance.deploy(dto);
    }
}
