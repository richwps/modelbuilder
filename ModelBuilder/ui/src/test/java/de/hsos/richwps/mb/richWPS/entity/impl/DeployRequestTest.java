package de.hsos.richwps.mb.richWPS.entity.impl;

import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputLiteralDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputLiteralDataDescription;
import java.util.ArrayList;
import java.util.List;
import net.opengis.wps.x100.ProcessDescriptionType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dalcacer
 */
public class DeployRequestTest {

    public DeployRequestTest() {
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

    private IInputSpecifier createComplexDataInput() {
        InputComplexDataDescription specifier;
        specifier = new InputComplexDataDescription();
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
        return specifier;
    }

    private IInputSpecifier createLiteralDataInput() {
        InputLiteralDataDescription specifier = new InputLiteralDataDescription();
        specifier.setIdentifier("aabb");
        specifier.setTitle("aabb");
        specifier.setAbstract("aabb's abstract");
        specifier.setMinOccur(0);
        specifier.setMaxOccur(1);
        specifier.setType(("xs:string"));
        specifier.setDefaultvalue("aab");
        return specifier;
    }

    private IOutputSpecifier createComplexDataOutput() {
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
        return specifier;
    }

    private IOutputSpecifier createLiteralDataOutput() {
        OutputLiteralDataDescription specifier = new OutputLiteralDataDescription();
        specifier.setIdentifier("aabb");
        specifier.setTitle("aabb");
        specifier.setAbstract("aabb's abstract");
        specifier.setType("xs:string");
        return specifier;
    }

    /**
     * Test of getEndpoint method, of class DeployRequestDTO.
     */
    @Test
    public void testCreation() {
        System.out.println("testCreation");
        DeployRequest request = new DeployRequest("localhost", "test", "test", "1.0", "ROLA");
        request.addInput(this.createComplexDataInput());
        request.addInput(this.createLiteralDataInput());
        request.addOutput(this.createComplexDataOutput());
        request.addOutput(this.createLiteralDataOutput());
        
        ProcessDescriptionType description = request.toProcessDescriptionType();
        assertNotNull(description);
    }
}
