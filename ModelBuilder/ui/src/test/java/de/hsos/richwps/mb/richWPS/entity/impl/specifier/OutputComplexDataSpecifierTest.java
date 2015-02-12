package de.hsos.richwps.mb.richWPS.entity.impl.specifier;

import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputComplexDataDescription;
import java.util.ArrayList;
import java.util.List;
import net.opengis.wps.x100.OutputDescriptionType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.n52.wps.client.richwps.OutputDescriptionTypeBuilder;

/**
 *
 * @author dalcacer
 */
public class OutputComplexDataSpecifierTest {
    
    public OutputComplexDataSpecifierTest() {
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
     * Test of getIdentifier method, of class OutputComplexDataDescription.
     */
    @Test
    public void testConstruction() {
        System.out.println("testConstruction");
        OutputComplexDataDescription specifier = new OutputComplexDataDescription();
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
        
        OutputDescriptionType description = specifier.toOutputDescription();
        OutputComplexDataDescription specifier2 = new OutputComplexDataDescription(description);
        assertEquals(specifier, specifier2);
    }
    
}
