package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.*;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.GetInputTypesRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.GetOutputTypesRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.GetProcessesRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.values.InputComplexDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.InputLiteralDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.OutputComplexDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputLiteralDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputLiteralDataDescription;
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
     *
     * public void testConnect() { System.out.println("testConnect");
     * RichWPSProvider instance = new RichWPSProvider(); try {
     * instance.connect(wpsurl); } catch (Exception e) { fail("Unable to
     * connect."); } }
     */
    /**
     * Test of connect method, of class RichWPSProvider.
     *
     * public void testConnectException() { System.out.println("testConnect");
     * RichWPSProvider instance = new RichWPSProvider(); try {
     * instance.connect("http://no"); } catch (Exception e) { assertNotNull(e);
     * return; } fail("Expected exception was not raised."); }
     */
   
    /**
     * Test of wpsGetAvailableProcesses method, of class RichWPSProvider.
     */
    public void testGetAvailableProcesses() {
        System.out.println("getAvailableProcesses");
        RichWPSProvider instance = new RichWPSProvider();

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
        GetProcessesRequest procrequest = new GetProcessesRequest(wpsurl);
        instance.perform(procrequest);
        List<String> result = procrequest.getProcesses();
        assertNotSame(new ArrayList<String>(), result); //not empty!
    }

    /**
     * Test of wpsDescribeProcess method, of class RichWPSProvider.
     */
    public void testDescribeProcess() {
        System.out.println("describeProcess");
        String processid = "org.n52.wps.server.algorithm.test.DummyTestClass";

        RichWPSProvider instance = new RichWPSProvider();

        ExecuteRequest request = new ExecuteRequest();
        request.setEndpoint(wpsurl);
        request.setIdentifier(processid);
        instance.perform((DescribeRequest) request);
        List<IInputDescription> inputs = request.getInputs();
        assertEquals(3, inputs.size()); //3 with BBOX support
        assertEquals("ComplexInputData", ((IInputDescription) inputs.get(0)).getIdentifier());
        assertEquals("LiteralInputData", ((IInputDescription) inputs.get(1)).getIdentifier());
        assertEquals("BBOXInputData", ((IInputDescription) inputs.get(2)).getIdentifier());

        List<IOutputValue> outputs = request.getOutputs();
        assertEquals(3, outputs.size()); //3 with BBOX support
        assertEquals("ComplexOutputData", ((IOutputValue) outputs.get(0)).getIdentifier());
        assertEquals("LiteralOutputData", ((IOutputValue) outputs.get(1)).getIdentifier());
        assertEquals("BBOXOutputData", ((IOutputValue) outputs.get(2)).getIdentifier());
    }

    public void testEchoProcess() {
        System.out.println("testEchoProcess");
        String processid = "org.n52.wps.server.algorithm.test.EchoProcess";

        RichWPSProvider instance = new RichWPSProvider();

        ExecuteRequest request = new ExecuteRequest();
        request.setEndpoint(wpsurl);
        request.setIdentifier(processid);

        instance.perform((DescribeRequest) request);
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

        instance.perform(request);

        HashMap<String, Object> theResults = request.getResults();
        assertNotNull(theResults);
    }

    /**
     * Should raise a server-side exception because of faulty inputs.
     *
     * public void testEchoProcessException() {
     * System.out.println("testEchoProcessException"); String processid =
     * "org.n52.wps.server.algorithm.test.EchoProcess";
     *
     * RichWPSProvider instance = new RichWPSProvider();
     *
     * ExecuteRequest perform = new ExecuteRequest();
     * perform.setEndpoint(wpsurl); perform.setIdentifier(processid);
     * instance.perform((DescribeRequest) perform); List<IInputSpecifier> inputs
     * = perform.getInputs();
     *
     * assertEquals(2, inputs.size()); InputComplexDataDescription geomSpec;
     * InputLiteralDataDescription literalSpec; if (inputs.get(0) instanceof
     * InputComplexDataDescription) { geomSpec = (InputComplexDataDescription)
     * inputs.get(0); literalSpec = (InputLiteralDataDescription) inputs.get(1);
     * } else { geomSpec = (InputComplexDataDescription) inputs.get(1);
     * literalSpec = (InputLiteralDataDescription) inputs.get(0); }
     *
     * HashMap<String, IInputValue> ins = new HashMap<>(); InputLiteralDataValue
     * arg1 = new InputLiteralDataValue(literalSpec, "Hello World.");
     * InputComplexDataValue arg2 = new InputComplexDataValue(geomSpec);
     * arg2.setAsReference(true); arg2.setURL("http://no.domain");
     * arg2.setMimeType("text/xml"); ins.put("literalInput", arg1);
     * ins.put("complexInput", arg2); perform.setInputValues(ins);
     *
     * HashMap<String, IOutputDescription> outs = new HashMap();
     * List<IOutputSpecifier> outputs = perform.getOutputs();
     * OutputComplexDataDescription outspec; if (outputs.get(0) instanceof
     * OutputComplexDataDescription) { outspec = (OutputComplexDataDescription)
     * outputs.get(0); } else { outspec = (OutputComplexDataDescription)
     * outputs.get(1); }
     *
     * OutputComplexDataValue outarg = new OutputComplexDataValue(outspec);
     * outarg.setAsReference(true); outarg.setMimetype("text/html");
     * outs.put("result", outarg);
     *
     * instance.perform(perform);
     *
     * assertTrue(perform.isException()); assertNotNull(perform.getException());
     * }
     */

    /*public void testSimpleBuffer() {
     System.out.println("testSimpleBuffer");
     String processid = "org.n52.wps.server.algorithm.SimpleBufferAlgorithm";

     RichWPSProvider instance = new RichWPSProvider();
     try {
     instance.connect(wpsurl);
     } catch (Exception ex) {
     Logger.getLogger(RichWPSProviderTest.class.getName()).log(Level.SEVERE, null, ex);
     }

     ExecuteRequest perform = new ExecuteRequest();
     perform.setEndpoint(wpsurl);
     perform.setIdentifier(processid);

     instance.wpsDescribeProcess(perform);
     List<IInputSpecifier> inputs = perform.getInputs();

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
     InputLiteralDataValue arg1 = new InputLiteralDataValue(literalSpec, "10");
     InputComplexDataValue arg2 = new InputComplexDataValue(geomSpec);
     arg2.setAsReference(true);
     arg2.setURL("http://map.ices.dk/geoserver/sf/ows?service=WFS&version=1.0.0&perform=GetFeature&typeName=sf:roads&maxFeatures=50");
     arg2.setMimeType("text/xml");
     ins.put("width", arg1);
     ins.put("data", arg2);
     perform.setInputValues(ins);

     HashMap<String, IOutputDescription> outs = new HashMap();
     List<IOutputSpecifier> outputs = perform.getOutputs();
     OutputComplexDataDescription outspec = (OutputComplexDataDescription) outputs.get(0);
     OutputComplexDataValue outarg = new OutputComplexDataValue(outspec);
     outarg.setAsReference(true);
     outarg.setMimetype("text/html");
     outs.put("result", outarg);

     instance.wpsExecuteProcess(perform);

     HashMap<String, Object> theResults = perform.getResults();
     assertNotNull(theResults);
     }*/
    private IInputDescription createComplexDataInput() {
        InputComplexDataDescription description;
        description = new InputComplexDataDescription();
        description.setIdentifier("aabb input.");
        description.setTitle("aabb input.");
        description.setMinOccur(0);
        description.setMaxOccur(1);
        description.setAbstract("aabb's abstract.");

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

        description.setTypes(types);
        description.setDefaulttype(atype);
        description.setMaximumMegabytes(5);
        return description;
    }

    private IInputDescription createLiteralDataInput() {
        InputLiteralDataDescription description = new InputLiteralDataDescription();
        description.setIdentifier("aabb");
        description.setTitle("aabb");
        description.setAbstract("aabb's abstract");
        description.setMinOccur(0);
        description.setMaxOccur(1);
        description.setType(("xs:string"));
        description.setDefaultvalue("aab");
        return description;
    }

    private IOutputValue createComplexDataOutput() {
        OutputComplexDataDescription description = new OutputComplexDataDescription();
        description.setIdentifier("aabb");
        description.setTitle("aabb");
        description.setTheAbstract("aabb's abstract");

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

        description.setTypes(types);
        description.setDefaulttype(atype);
        return description;
    }

    private IOutputValue createLiteralDataOutput() {
        OutputLiteralDataDescription description = new OutputLiteralDataDescription();
        description.setIdentifier("identifier");
        description.setTitle("");
        description.setAbstract("identifier {NF/DI}.");
        description.setType("xs:string");
        return description;
    }

    /*public void testDeploy() {
     System.out.println("testDeploy");

     RichWPSProvider instance = new RichWPSProvider();
     try {
     instance.connect(wpsurl, wpsurl);
     } catch (Exception ex) {
     Logger.getLogger(RichWPSProviderTest.class.getName()).log(Level.SEVERE, null, ex);
     }

     DeployRequest perform = new DeployRequest("localhost", "test", "test", "1.0", RichWPSProvider.deploymentProfile);
     perform.addInput(this.createComplexDataInput());
     perform.addInput(this.createLiteralDataInput());
     perform.addOutput(this.createComplexDataOutput());
     perform.addOutput(this.createLiteralDataOutput());
     perform.setExecutionUnit("var.identifier = in.identifier\n" +
     "bind process lkn.macrophyte.selectReportingArea to local/lkn.macrophyte.selectReportingArea\n" +
     "execute local/lkn.macrophyte.selectReportingArea with var.reportingareas as in.reportingareas var.identifier as in.areaname  store out.selectedarea as var.out.selectedarea");
     instance.richwpsDeployProcess(perform);
     }*/
    private IInputDescription createComplexDataInput1() {
        InputComplexDataDescription description;
        description = new InputComplexDataDescription();
        description.setIdentifier("reportingareas");
        description.setTitle("");
        description.setMinOccur(1);
        description.setMaxOccur(1);
        description.setAbstract(".");

        List<String> atype = new ArrayList<>();
        atype.add("application/json");   // mimetype
        atype.add("");  // schema
        atype.add("");  // encoding

        List<List> types = new ArrayList<>();
        types.add(atype);

        description.setTypes(types);
        description.setDefaulttype(atype);
        description.setMaximumMegabytes(50);
        return description;
    }

    private IInputDescription createLiteralDataInput1() {
        InputLiteralDataDescription description = new InputLiteralDataDescription();
        description.setIdentifier("identifier");
        description.setTitle("");
        description.setAbstract("{NF/DI}");
        description.setMinOccur(1);
        description.setMaxOccur(1);
        description.setType(("xs:string"));
        description.setDefaultvalue("NF");
        return description;
    }

    private IOutputValue createComplexDataOutput1() {
        OutputComplexDataDescription description = new OutputComplexDataDescription();
        description.setIdentifier("selectedarea");
        description.setTitle("");
        description.setTheAbstract("");

        List<String> atype = new ArrayList<>();
        atype.add("application/xml");   // mimetype
        atype.add("");  // schema
        atype.add("");  // encoding

        List<List> types = new ArrayList<>();
        types.add(atype);

        description.setTypes(types);
        description.setDefaulttype(atype);
        return description;
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
//        DeployRequest perform = new DeployRequest(wpsurl, richwpsurl, "Testprocess", "A title", "2", RichWPSProvider.deploymentProfile);
//        perform.addInput(this.createComplexDataInput1());
//        perform.addInput(this.createLiteralDataInput1());
//        perform.addOutput(this.createComplexDataOutput1());
//        perform.setExecutionUnit("var.identifier = in.identifier\n"
//                + "bind process lkn.macrophyte.selectReportingArea to local/lkn.macrophyte.selectReportingArea\n"
//                + "execute local/lkn.macrophyte.selectReportingArea with var.reportingareas as in.reportingareas var.identifier as in.areaname  store out.selectedarea as var.out.selectedarea ");
//        instance.richwpsDeployProcess(perform);
//        assertEquals(perform.isException(), false);
//
//        System.out.println("performing undeploy");
//        UndeployRequest unrequest = new UndeployRequest(wpsurl, richwpsurl, "Testprocess");
//        instance.richwpsUndeployProcess(unrequest);
//        assertEquals(unrequest.isException(), false);
    }

    public void testGetInputTypes() {
        System.out.println("testGetInputTypes");
        String wpsurl = "http://richwps.edvsz.hs-osnabrueck.de/lkn/WebProcessingService";
        String richwpsurl = "http://richwps.edvsz.hs-osnabrueck.de/lkn/RichWPS";
        RichWPSProvider instance = new RichWPSProvider();
        GetInputTypesRequest request = new GetInputTypesRequest(wpsurl, richwpsurl);
        try {
            instance.perform(request);
            List formats = request.getFormats();
            System.out.println(formats);
            assertNotNull(formats);
        } catch (Exception ex) {
            Logger.getLogger(RichWPSProviderTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }

    public void testGetOutputTypes() {
        System.out.println("testGetOutputTypes");
        String wpsurl = "http://richwps.edvsz.hs-osnabrueck.de/lkn/WebProcessingService";
        String richwpsurl = "http://richwps.edvsz.hs-osnabrueck.de/lkn/RichWPS";
        RichWPSProvider instance = new RichWPSProvider();
        GetOutputTypesRequest request = new GetOutputTypesRequest(wpsurl, richwpsurl);
        try {
            instance.perform(request);
            List formats = request.getFormats();
            System.out.println(formats);
            assertNotNull(formats);
        } catch (Exception ex) {
            Logger.getLogger(RichWPSProviderTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
