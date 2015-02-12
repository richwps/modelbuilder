package de.hsos.richwps.mb.richWPS.entity.impl.specifier;

import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputLiteralDataDescription;
import net.opengis.wps.x100.InputDescriptionType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.n52.wps.client.richwps.InputDescriptionTypeBuilder;

/**
 *
 * @author dalcacer
 */
public class InputLiteralDataSpecifierTest {
    
    public InputLiteralDataSpecifierTest() {
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
     * Test of getIdentifier method, of class InputLiteralDataDescription.
     */
    @Test
    public void testConstruction() {
        System.out.println("testConstruction");
        InputLiteralDataDescription specifier = new InputLiteralDataDescription();
        specifier.setIdentifier("aabb");
        specifier.setTitle("aabb");
        specifier.setAbstract("aabb's abstract");
        specifier.setMinOccur(0);
        specifier.setMaxOccur(1);
        specifier.setType(("xs:string"));
        specifier.setDefaultvalue("aab");
        
        InputDescriptionType ogctype = specifier.toInputDescription();
        InputLiteralDataDescription specifier2 = new InputLiteralDataDescription(ogctype);
        assertEquals(specifier, specifier2);
    }
}
