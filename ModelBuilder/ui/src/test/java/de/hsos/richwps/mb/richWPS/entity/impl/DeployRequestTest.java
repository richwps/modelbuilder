package de.hsos.richwps.mb.richWPS.entity.impl;

import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputLiteralDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputLiteralDataSpecifier;
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
        return specifier;
    }

    private IInputSpecifier createLiteralDataInput() {
        InputLiteralDataSpecifier specifier = new InputLiteralDataSpecifier();
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
        OutputComplexDataSpecifier specifier = new OutputComplexDataSpecifier();
        specifier.setIdentifier("aabb");
        specifier.setTitle("aabb");
        specifier.setTheabstract("aabb's abstract");
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
        OutputLiteralDataSpecifier specifier = new OutputLiteralDataSpecifier();
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
