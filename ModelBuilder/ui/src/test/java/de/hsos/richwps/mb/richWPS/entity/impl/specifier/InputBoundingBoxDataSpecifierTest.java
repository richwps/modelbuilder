package de.hsos.richwps.mb.richWPS.entity.impl.specifier;

import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputBoundingBoxDataDescription;
import net.opengis.wps.x100.InputDescriptionType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.n52.wps.client.richwps.InputDescriptionTypeBuilder;

/**
 *  BoundingBoxDataSpecifier Test.
 *  @author caduevel
 */
public class InputBoundingBoxDataSpecifierTest {
    
    public InputBoundingBoxDataSpecifierTest() {
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
     * Test of getIdentifier method, of class InputBoundingBoxSpecifier.
     */
    @Test
    public void testConstruction() {
        System.out.println("testConstruction");
        InputBoundingBoxDataDescription specifier;
        specifier = new InputBoundingBoxDataDescription();
        specifier.setIdentifier("aabb");
        specifier.setTitle("aabb");
        specifier.setAbstract("aabb's abstract");
        specifier.setMinOccur(0);
        specifier.setMaxOccur(1);
        specifier.setDefaultCRS("DefaultCRS");
        
        InputDescriptionType ogctype = specifier.toInputDescription();
        InputBoundingBoxDataDescription specifier2;
        specifier2 = new InputBoundingBoxDataDescription(ogctype);
        assertEquals(specifier, specifier2);
    }
}
