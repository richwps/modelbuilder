package de.hsos.richwps.mb.richWPS.entity.deploy;

import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.specifier.InputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.specifier.InputLiteralDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.specifier.OutputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.specifier.OutputLiteralDataSpecifier;
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
public class DeployRequestDTOTest {

    public DeployRequestDTOTest() {
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
        DeployRequestDTO dto = new DeployRequestDTO("localhost", "test", "test", "1.0");
        dto.addInput(this.createComplexDataInput());
        dto.addInput(this.createLiteralDataInput());
        dto.addOutput(this.createComplexDataOutput());
        dto.addOutput(this.createLiteralDataOutput());
        
        ProcessDescriptionType description = dto.toProcessDescriptionType();
        assertNotNull(description);
    }

}
