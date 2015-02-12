package de.hsos.richwps.mb.richWPS.entity.impl.specifier;

import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputBoundingBoxDataDescription;
import net.opengis.wps.x100.OutputDescriptionType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author caduevel
 */
public class OutputBoundingBoxDataSpecifierTest {
    
    public OutputBoundingBoxDataSpecifierTest() {
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
     * Test of equals method, of class OutputBoundingBoxDataDescription.
     */
    @Test
    public void testConstruction() {
        System.out.println("testConstruction");
        OutputBoundingBoxDataDescription specifier;
        specifier = new OutputBoundingBoxDataDescription();
        specifier.setIdentifier("aabb");
        specifier.setTitle("aabb");
        specifier.setAbstract("aabb's abstract");
        specifier.setDefaultCRS("aaba");
        
        OutputDescriptionType description = specifier.toOutputDescription();
        OutputBoundingBoxDataDescription specificer2;
        specificer2 = new OutputBoundingBoxDataDescription(description);
        assertEquals(specifier, specificer2);
    }
    
}
