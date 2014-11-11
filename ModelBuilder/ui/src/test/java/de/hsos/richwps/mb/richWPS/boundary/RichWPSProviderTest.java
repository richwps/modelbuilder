package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.*;
import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest;
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

    //private String wpsurl = "http://geoprocessing.demo.52north.org:8080/wps/WebProcessingService";
    private String wpsurl = "http://richwps.edvsz.hs-osnabrueck.de/wps/WebProcessingService";

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

        ExecuteRequest request = new ExecuteRequest();
        request.setEndpoint(wpsurl);
        request.setIdentifier(processid);
        instance.describeProcess(request);
        List<IInputSpecifier> inputs = request.getInputs();
        assertEquals(3, inputs.size()); //3 with BBOX support
        assertEquals("ComplexInputData", ((IInputSpecifier) inputs.get(0)).getIdentifier());
        assertEquals("LiteralInputData", ((IInputSpecifier) inputs.get(1)).getIdentifier());
        assertEquals("BBOXInputData", ((IInputSpecifier) inputs.get(2)).getIdentifier());

        List<IOutputSpecifier> outputs = request.getOutputs();
        assertEquals(3, outputs.size()); //3 with BBOX support
        assertEquals("ComplexOutputData", ((IOutputSpecifier) outputs.get(0)).getIdentifier());
        assertEquals("LiteralOutputData", ((IOutputSpecifier) outputs.get(1)).getIdentifier());
        assertEquals("BBOXOutputData", ((IOutputSpecifier)outputs.get(2)).getIdentifier());   
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

        ExecuteRequest request = new ExecuteRequest();
        request.setEndpoint(wpsurl);
        request.setIdentifier(processid);

        instance.describeProcess(request);
        List<IInputSpecifier> inputs = request.getInputs();

        assertEquals(2, inputs.size());
        InputComplexDataSpecifier geomSpec;
        InputLiteralDataSpecifier literalSpec;
        if (inputs.get(0) instanceof InputComplexDataSpecifier) {
            geomSpec = (InputComplexDataSpecifier) inputs.get(0);
            literalSpec = (InputLiteralDataSpecifier) inputs.get(1);
        } else {
            geomSpec = (InputComplexDataSpecifier) inputs.get(1);
            literalSpec = (InputLiteralDataSpecifier) inputs.get(0);
        }

        HashMap<String, IInputArgument> ins = new HashMap<>();
        InputLiteralDataArgument arg1 = new InputLiteralDataArgument(literalSpec, "Hello World.");
        InputComplexDataArgument arg2 = new InputComplexDataArgument(geomSpec);
        arg2.setAsReference(true);
        arg2.setURL("http://map.ices.dk/geoserver/sf/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=sf:roads&maxFeatures=50");
        arg2.setMimeType("text/xml");
        ins.put("literalInput", arg1);
        ins.put("complexInput", arg2);
        request.setInputArguments(ins);

        HashMap<String, IOutputArgument> outs = new HashMap();
        List<IOutputSpecifier> outputs = request.getOutputs();
        OutputComplexDataSpecifier outspec;
        if(outputs.get(0) instanceof OutputComplexDataSpecifier){
            outspec = (OutputComplexDataSpecifier) outputs.get(0);
        }else{
            outspec = (OutputComplexDataSpecifier) outputs.get(1);
        }
        
        OutputComplexDataArgument outarg = new OutputComplexDataArgument(outspec);
        outarg.setAsReference(true);
        outarg.setMimetype("text/html");
        outs.put("result", outarg);

        instance.executeProcess(request);

        HashMap<String, Object> theResults = request.getResults();
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

        ExecuteRequest request = new ExecuteRequest();
        request.setEndpoint(wpsurl);
        request.setIdentifier(processid);

        instance.describeProcess(request);
        List<IInputSpecifier> inputs = request.getInputs();

        assertEquals(2, inputs.size());
        InputComplexDataSpecifier geomSpec;
        InputLiteralDataSpecifier literalSpec;
        if (inputs.get(0) instanceof InputComplexDataSpecifier) {
            geomSpec = (InputComplexDataSpecifier) inputs.get(0);
            literalSpec = (InputLiteralDataSpecifier) inputs.get(1);
        } else {
            geomSpec = (InputComplexDataSpecifier) inputs.get(1);
            literalSpec = (InputLiteralDataSpecifier) inputs.get(0);
        }

        HashMap<String, IInputArgument> ins = new HashMap<>();
        InputLiteralDataArgument arg1 = new InputLiteralDataArgument(literalSpec, "Hello World.");
        InputComplexDataArgument arg2 = new InputComplexDataArgument(geomSpec);
        arg2.setAsReference(true);
        arg2.setURL("http://no.domain");
        arg2.setMimeType("text/xml");
        ins.put("literalInput", arg1);
        ins.put("complexInput", arg2);
        request.setInputArguments(ins);

        HashMap<String, IOutputArgument> outs = new HashMap();
        List<IOutputSpecifier> outputs = request.getOutputs();
        OutputComplexDataSpecifier outspec;
        if(outputs.get(0) instanceof OutputComplexDataSpecifier){
            outspec = (OutputComplexDataSpecifier) outputs.get(0);
        }else{
            outspec = (OutputComplexDataSpecifier) outputs.get(1);
        }
        
        OutputComplexDataArgument outarg = new OutputComplexDataArgument(outspec);
        outarg.setAsReference(true);
        outarg.setMimetype("text/html");
        outs.put("result", outarg);

        instance.executeProcess(request);

        assertTrue(request.isException());
        assertNotNull(request.getException());
    }

    /*public void testSimpleBuffer() {
     System.out.println("testSimpleBuffer");
     String processid = "org.n52.wps.server.algorithm.SimpleBufferAlgorithm";

     RichWPSProvider instance = new RichWPSProvider();
     try {
     instance.connect(wpsurl);
     } catch (Exception ex) {
     Logger.getLogger(RichWPSProviderTest.class.getName()).log(Level.SEVERE, null, ex);
     }

     ExecuteRequest request = new ExecuteRequest();
     request.setEndpoint(wpsurl);
     request.setIdentifier(processid);

     instance.describeProcess(request);
     List<IInputSpecifier> inputs = request.getInputs();

     assertEquals(2, inputs.size());
     InputComplexDataSpecifier geomSpec;
     InputLiteralDataSpecifier literalSpec;
     if (inputs.get(0) instanceof InputComplexDataSpecifier) {
     geomSpec = (InputComplexDataSpecifier) inputs.get(0);
     literalSpec = (InputLiteralDataSpecifier) inputs.get(1);
     } else {
     geomSpec = (InputComplexDataSpecifier) inputs.get(1);
     literalSpec = (InputLiteralDataSpecifier) inputs.get(0);
     }

     HashMap<String, IInputArgument> ins = new HashMap<>();
     InputLiteralDataArgument arg1 = new InputLiteralDataArgument(literalSpec, "10");
     InputComplexDataArgument arg2 = new InputComplexDataArgument(geomSpec);
     arg2.setAsReference(true);
     arg2.setURL("http://map.ices.dk/geoserver/sf/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=sf:roads&maxFeatures=50");
     arg2.setMimeType("text/xml");
     ins.put("width", arg1);
     ins.put("data", arg2);
     request.setInputArguments(ins);

     HashMap<String, IOutputArgument> outs = new HashMap();
     List<IOutputSpecifier> outputs = request.getOutputs();
     OutputComplexDataSpecifier outspec = (OutputComplexDataSpecifier) outputs.get(0);
     OutputComplexDataArgument outarg = new OutputComplexDataArgument(outspec);
     outarg.setAsReference(true);
     outarg.setMimetype("text/html");
     outs.put("result", outarg);

     instance.executeProcess(request);

     HashMap<String, Object> theResults = request.getResults();
     assertNotNull(theResults);
     }*/
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
        specifier.setTheAbstract("aabb's abstract");

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
        specifier.setIdentifier("identifier");
        specifier.setTitle("");
        specifier.setAbstract("identifier {NF/DI}.");
        specifier.setType("xs:string");
        return specifier;
    }

    /*public void testDeploy() {
     System.out.println("testDeploy");

     RichWPSProvider instance = new RichWPSProvider();
     try {
     instance.connect(wpsurl, wpsurl);
     } catch (Exception ex) {
     Logger.getLogger(RichWPSProviderTest.class.getName()).log(Level.SEVERE, null, ex);
     }

     DeployRequest request = new DeployRequest("localhost", "test", "test", "1.0", RichWPSProvider.deploymentProfile);
     request.addInput(this.createComplexDataInput());
     request.addInput(this.createLiteralDataInput());
     request.addOutput(this.createComplexDataOutput());
     request.addOutput(this.createLiteralDataOutput());
     request.setExecutionUnit("var.identifier = in.identifier\n" +
     "bind process lkn.macrophyte.selectReportingArea to local/lkn.macrophyte.selectReportingArea\n" +
     "execute local/lkn.macrophyte.selectReportingArea with var.reportingareas as in.reportingareas var.identifier as in.areaname  store out.selectedarea as var.out.selectedarea");
     instance.deployProcess(request);
     }*/
    private IInputSpecifier createComplexDataInput1() {
        InputComplexDataSpecifier specifier;
        specifier = new InputComplexDataSpecifier();
        specifier.setIdentifier("reportingareas");
        specifier.setTitle("");
        specifier.setMinOccur(1);
        specifier.setMaxOccur(1);
        specifier.setAbstract(".");

        List<String> atype = new ArrayList<>();
        atype.add("application/json");   // mimetype
        atype.add("");  // schema
        atype.add("");  // encoding

        List<List> types = new ArrayList<>();
        types.add(atype);

        specifier.setTypes(types);
        specifier.setDefaulttype(atype);
        specifier.setMaximumMegabytes(50);
        return specifier;
    }

    private IInputSpecifier createLiteralDataInput1() {
        InputLiteralDataSpecifier specifier = new InputLiteralDataSpecifier();
        specifier.setIdentifier("identifier");
        specifier.setTitle("");
        specifier.setAbstract("{NF/DI}");
        specifier.setMinOccur(1);
        specifier.setMaxOccur(1);
        specifier.setType(("xs:string"));
        specifier.setDefaultvalue("NF");
        return specifier;
    }

    private IOutputSpecifier createComplexDataOutput1() {
        OutputComplexDataSpecifier specifier = new OutputComplexDataSpecifier();
        specifier.setIdentifier("selectedarea");
        specifier.setTitle("");
        specifier.setTheAbstract("");

        List<String> atype = new ArrayList<>();
        atype.add("application/xml");   // mimetype
        atype.add("");  // schema
        atype.add("");  // encoding

        List<List> types = new ArrayList<>();
        types.add(atype);

        specifier.setTypes(types);
        specifier.setDefaulttype(atype);
        return specifier;
    }

    public void testDeployUndeploy() {
        System.out.println("testDeployUndeploy");
//        String wpsurl = "http://richwps.edvsz.hs-osnabrueck.de/lkn/WebProcessingService";
//        String richwpsurl = "http://richwps.edvsz.hs-osnabrueck.de/lkn/RichWPS";
//        RichWPSProvider instance = new RichWPSProvider();
//        try {
//            instance.connect(wpsurl, richwpsurl);
//        } catch (Exception ex) {
//            Logger.getLogger(RichWPSProviderTest.class.getName()).log(Level.SEVERE, null, ex);
//            fail();
//        }
//
//        System.out.println("performing deploy");
//        DeployRequest request = new DeployRequest(wpsurl, richwpsurl, "Testprocess", "A title", "2", RichWPSProvider.deploymentProfile);
//        request.addInput(this.createComplexDataInput1());
//        request.addInput(this.createLiteralDataInput1());
//        request.addOutput(this.createComplexDataOutput1());
//        request.setExecutionUnit("var.identifier = in.identifier\n"
//                + "bind process lkn.macrophyte.selectReportingArea to local/lkn.macrophyte.selectReportingArea\n"
//                + "execute local/lkn.macrophyte.selectReportingArea with var.reportingareas as in.reportingareas var.identifier as in.areaname  store out.selectedarea as var.out.selectedarea ");
//        instance.deployProcess(request);
//        assertEquals(request.isException(), false);
//
//        System.out.println("performing undeploy");
//        UndeployRequest unrequest = new UndeployRequest(wpsurl, richwpsurl, "Testprocess");
//        instance.undeployProcess(unrequest);
//        assertEquals(unrequest.isException(), false);
    }
    
    public void testGetInputTypes(){
        System.out.println("testGetInputTypes");
        String wpsurl = "http://richwps.edvsz.hs-osnabrueck.de/lkn/WebProcessingService";
        String richwpsurl = "http://richwps.edvsz.hs-osnabrueck.de/lkn/RichWPS";
        RichWPSProvider instance = new RichWPSProvider();
        try {
            instance.connect(wpsurl, richwpsurl);
            List formats = instance.getInputTypes(wpsurl);
            System.out.println(formats);
            assertNotNull(formats);
        } catch (Exception ex) {
            Logger.getLogger(RichWPSProviderTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    public void testGetOutputTypes(){
        System.out.println("testGetOutputTypes");
        String wpsurl = "http://richwps.edvsz.hs-osnabrueck.de/lkn/WebProcessingService";
        String richwpsurl = "http://richwps.edvsz.hs-osnabrueck.de/lkn/RichWPS";
        RichWPSProvider instance = new RichWPSProvider();
        try {
            instance.connect(wpsurl, richwpsurl);
            List formats = instance.getOutputTypes(wpsurl);
            System.out.println(formats);
            assertNotNull(formats);
        } catch (Exception ex) {
            Logger.getLogger(RichWPSProviderTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
