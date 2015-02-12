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
public class OutputLiteralDataDescriptionTest {
    
    public OutputLiteralDataDescriptionTest() {
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
        OutputLiteralDataDescription desc = new OutputLiteralDataDescription();
        desc.setIdentifier("aabb");
        desc.setTitle("aabb");
        desc.setAbstract("aabb's abstract");
        desc.setType("xs:string");
        
        OutputDescriptionType description = desc.toOutputDescription();
        OutputLiteralDataDescription desc2 = new OutputLiteralDataDescription(description);
        assertEquals(desc, desc2);
    }
    
}
