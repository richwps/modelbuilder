package de.hsos.richwps.mb.richWPS.entity.impl.specifier;

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
     * Test of equals method, of class OutputBoundingBoxDataSpecifier.
     */
    @Test
    public void testConstruction() {
        System.out.println("testConstruction");
        OutputBoundingBoxDataSpecifier specifier;
        specifier = new OutputBoundingBoxDataSpecifier();
        specifier.setIdentifier("aabb");
        specifier.setTitle("aabb");
        specifier.setAbstract("aabb's abstract");
        specifier.setDefaultCRS("aaba");
        
        OutputDescriptionType description = specifier.toOutputDescription();
        OutputBoundingBoxDataSpecifier specificer2;
        specificer2 = new OutputBoundingBoxDataSpecifier(description);
        assertEquals(specifier, specificer2);
    }
    
}
