package de.hsos.richwps.mb.richWPS.entity.impl.specifier;

import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputLiteralDataDescription;
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
public class OutputLiteralDataSpecifierTest {
    
    public OutputLiteralDataSpecifierTest() {
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
     * Test of getIdentifier method, of class OutputLiteralDataDescription.
     */
    @Test
    public void testConstruction() {
        System.out.println("testConstruction");
        OutputLiteralDataDescription specifier = new OutputLiteralDataDescription();
        specifier.setIdentifier("aabb");
        specifier.setTitle("aabb");
        specifier.setAbstract("aabb's abstract");
        specifier.setType("xs:string");
        
        OutputDescriptionType description = specifier.toOutputDescription();
        OutputLiteralDataDescription specificer2 = new OutputLiteralDataDescription(description);
        assertEquals(specifier, specificer2);
    }
    
}
