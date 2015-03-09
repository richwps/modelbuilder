/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView;

import com.mxgraph.model.mxCell;
import de.hsos.richwps.mb.app.AppSetup;
import de.hsos.richwps.mb.entity.datatypes.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.OwsObjectWithProperties;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.entity.ports.BoundingBoxInput;
import de.hsos.richwps.mb.entity.ports.ComplexDataOutput;
import de.hsos.richwps.mb.entity.ports.LiteralOutput;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author dziegenh
 */
public class GraphViewTest {

    private GraphView instance;
    private String processEntityServer;
    private String processEntityIdentifier;
    private String processEntityAbstract;
    private String processEntityTitle;
    private String inPort1Identifier;
    private String inPort1Abstract;
    private String inPort1Title;
    private String complexDescriptionFormatMime;
    private String complexDescriptionFormatSchema;
    private String complexDescriptionFormatEncoding;
    private ComplexDataTypeFormat complexDescriptionFormat;
    private DataTypeDescriptionComplex complexDescription;
    private String outPort1Title;
    private String outPort1Identifier;
    private String outPort1Abstract;
    private ProcessEntity process;
    private ProcessPort inPort1;
    private ProcessPort outPort1;
    private String globalOutPort1Identifier;
    private String globalOutPort1Abstract;
    private String globalOutPort1Title;
    private ProcessPort globalOut1;
    private String filename;


    public GraphViewTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        AppSetup.addGraphCodecs();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        filename = "testmodel.xml";
        deleteTestfile();

        instance = new GraphView();

        // PROCESS DATA
        processEntityServer = "PE server";
        processEntityIdentifier = "PE id";
        processEntityAbstract = "PE abstract";
        processEntityTitle = "PE title";

        // IN PORT 1 DATA
        inPort1Identifier = "PPIN1 id";
        inPort1Abstract = "PPIN1 abstract";
        inPort1Title = "PPIN1 title";

        // COMPLEX DATA
        complexDescriptionFormatMime = "CDTF mime";
        complexDescriptionFormatSchema = "CDTF schema";
        complexDescriptionFormatEncoding = "CDTF encoding";
        complexDescriptionFormat = new ComplexDataTypeFormat(complexDescriptionFormatMime, complexDescriptionFormatSchema, complexDescriptionFormatEncoding);
        complexDescription = new DataTypeDescriptionComplex(complexDescriptionFormat);

        // OUT PORT 1 DATA
        outPort1Identifier = "PPOUT1 id";
        outPort1Abstract = "PPOUT1 abstract";
        outPort1Title = "PPOUT1 title";

        // GLOBAL OUT 1 DATA
        globalOutPort1Identifier = "GLOBOUT1 id";
        globalOutPort1Abstract = "GLOBOUT1 abstract";
        globalOutPort1Title = "GLOBOUT1 title";

        // CREATE PROCESS
        process = new ProcessEntity(processEntityServer, processEntityIdentifier);
        process.setOwsAbstract(processEntityAbstract);
        process.setOwsTitle(processEntityTitle);

        // CREATE IN PORT
        inPort1 = new BoundingBoxInput();
        inPort1.setOwsIdentifier(inPort1Identifier);
        inPort1.setOwsAbstract(inPort1Abstract);
        inPort1.setOwsTitle(inPort1Title);
        process.addInputPort(inPort1);

        // CREATE OUT PORT
        outPort1 = new LiteralOutput();
        outPort1.setOwsIdentifier(outPort1Identifier);
        outPort1.setOwsAbstract(outPort1Abstract);
        outPort1.setOwsTitle(outPort1Title);
        process.addOutputPort(outPort1);

        // CREATE GLOBAL OUT
        globalOut1 = new ComplexDataOutput(true);
        globalOut1.setOwsIdentifier(globalOutPort1Identifier);
        globalOut1.setOwsAbstract(globalOutPort1Abstract);
        globalOut1.setOwsTitle(globalOutPort1Title);
    }

    private void deleteTestfile() {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }

    @After
    public void tearDown() {
        deleteTestfile();
    }

    /**
     * Test of createNodeFromProcess method, of class GraphView.
     */
    @Test
    public void testCreateNodeFromProcess() {
        System.out.println("createNodeFromProcess");
        ProcessEntity result = addProcessToInstance();
        assertNotNull(result);
        assertEquals(process, result);
    }

    private ProcessEntity addProcessToInstance() {
        Point location = new Point(100, 100);
        mxCell result = instance.createNodeFromProcess(process, location);
        return (ProcessEntity) result.getValue();
    }

    /**
     * Test of createNodeFromPort method, of class GraphView.
     */
    @Test
    public void testCreateNodeFromPort() {
        System.out.println("createNodeFromPort");
        ProcessPort result = addPortToInstance();
        assertNotNull(result);
        assertPortsEqual(globalOut1, result);
    }

    private ProcessPort addPortToInstance() {
        Point location = new Point(10, 50);
        mxCell createdNode = instance.createNodeFromPort(globalOut1, location);
        return (ProcessPort) createdNode.getValue();
    }

    /**
     * Test of saveGraphToXml method, of class GraphView.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSaveGraphToXml() throws Exception {
        System.out.println("saveGraphToXml");
        saveGraphToXml();
    }

    private void saveGraphToXml() throws IOException {
        // add nodes to instance
        addProcessToInstance();
        addPortToInstance();

        // TODO add model properties !!
        instance.saveGraphToXml(filename);
    }

    /**
     * Test of loadGraphFromXml method, of class GraphView.
     */
    @Test
    @Ignore
    public void testLoadGraphFromXml() throws Throwable {
        System.out.println("loadGraphFromXml");

        saveGraphToXml();

        File modelFile = new File(filename);
        if (modelFile.exists()) {
            instance.loadGraphFromXml(filename);

            Throwable theErr = null;

            try {
                // test process entity
                List<ProcessEntity> processes = instance.getGraph().getProcesses();
                assertProcessesEqual(process, processes.get(0));

                // test global port
                List<ProcessPort> globalOutputPorts = instance.getGraph().getGlobalOutputPorts();
                assertPortsEqual(globalOut1, globalOutputPorts.get(0));
            } catch (Throwable err) {
                theErr = err;
            }

            modelFile.delete();

            if (null != theErr) {
                throw theErr;
            }

        } else {
            System.out.println("WARNING: file '" + filename + "' doesn't exist!! Test not run !!");
        }

    }

    private void assertProcessesEqual(ProcessEntity process1, ProcessEntity process2) {
        List<String> keys = new LinkedList<>();
        keys.add(ProcessEntity.PROPERTIES_KEY_VERSION);
        keys.add(ProcessEntity.PROPERTIES_KEY_SERVER);

        assertOwsObjectsEqual(process1, process2, keys);
    }

    private void assertPortsEqual(ProcessPort port1, ProcessPort port2) {
        List<String> keys = new LinkedList<>();
//        keys.add(ProcessPort.PROPERTY_KEY_DATATYPEDESCRIPTION);

        assertOwsObjectsEqual(port1, port2, keys);
    }

    private void assertOwsObjectsEqual(OwsObjectWithProperties obj1, OwsObjectWithProperties obj2, List<String> propertyKeys) {
        final String ERR_MSG = "OwsObjectWithProperties entities are not equal";

        if (null == obj1 ^ null == obj2) {
            throw new AssertionError(ERR_MSG + " (one is null)");
        }

        propertyKeys.add(OwsObjectWithProperties.PROPERTIES_KEY_IDENTIFIER);
        propertyKeys.add(OwsObjectWithProperties.PROPERTIES_KEY_ABSTRACT);
        propertyKeys.add(OwsObjectWithProperties.PROPERTIES_KEY_TITLE);

        for (String key : propertyKeys) {
            Object obj1Value = obj1.getPropertyValue(key);
            Object obj2Value = obj2.getPropertyValue(key);

            boolean oneNull = (null == obj1Value ^ null == obj2Value);
            boolean bothNull = (null == obj1Value && null == obj2Value);

            if (oneNull || (!bothNull && !obj1Value.equals(obj2Value))) {
                throw new AssertionError(ERR_MSG + "(property " + key + ")");
            }
        }
    }

}
