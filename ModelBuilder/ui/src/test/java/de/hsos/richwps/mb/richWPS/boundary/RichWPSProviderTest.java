package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.richWPS.entity.execute.ExecuteRequestDTO;
import java.util.ArrayList;
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
        instance.connect(wpsurl);
    }

  
    /**
     * Test of getAvailableProcesses method, of class RichWPSProvider.
     */
    public void testGetAvailableProcesses() {
        System.out.println("getAvailableProcesses");
        RichWPSProvider instance = new RichWPSProvider();
        List<String> expResult = new ArrayList<String>();
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
        expResult.add("org.n52.wps.server.algorithm.convexhull.ConvexHullAlgorithm");
        expResult.add("org.n52.wps.extension.GetFuelPriceProcess");
        expResult.add("org.envirocar.wps.TrackToCSVProcess");
        expResult.add("org.envirocar.wps.HarvestAllTracksProcess");
        List<String> result = instance.getAvailableProcesses(wpsurl);
        assertEquals(expResult, result);

    }

    /**
     * Test of describeProcess method, of class RichWPSProvider.
     */
    public void testDescribeProcess() {
        System.out.println("describeProcess");
        String processid = "org.n52.wps.server.algorithm.JTSConvexHullAlgorithm";
        RichWPSProvider instance = new RichWPSProvider();
        ExecuteRequestDTO dto = new ExecuteRequestDTO();
        dto.setEndpoint(wpsurl);
        dto.setProcessid(processid);
        ExecuteRequestDTO result = instance.describeProcess(dto);
        //assertEquals(expResult, result);

    }
    
}
