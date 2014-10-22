package de.hsos.richwps.mb.richWPS.entity.impl.specifier;

import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputComplexDataSpecifier;
import java.util.ArrayList;
import java.util.List;
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
public class InputComplexDataSpecifierTest {

    public InputComplexDataSpecifierTest() {
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
     * Test of getIdentifier method, of class InputComplexDataSpecifier.
     */
    @Test
    public void testConstruction() {
        System.out.println("testConstruction");
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

        InputDescriptionType ogctype = specifier.toInputDescription();
        InputComplexDataSpecifier specifier2 = new InputComplexDataSpecifier(ogctype);

        assertEquals(specifier2, specifier);
    }

}
