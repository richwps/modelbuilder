/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.richWPS.entity.specifier;

import java.util.ArrayList;
import java.util.List;
import net.opengis.wps.x100.OutputDescriptionType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.n52.wps.client.transactional.BasicOutputDescriptionType;

/**
 *
 * @author dalcacer
 */
public class OutputComplexDataSpecifierTest {
    
    public OutputComplexDataSpecifierTest() {
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
     * Test of getIdentifier method, of class OutputComplexDataSpecifier.
     */
    @Test
    public void testConstruction() {
        System.out.println("testConstruction");
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
        
        BasicOutputDescriptionType description = specifier.toBasicOutputDescriptionType();
        OutputComplexDataSpecifier specifier2 = new OutputComplexDataSpecifier(description.getOdt());
        assertEquals(specifier, specifier2);
    }
    
}
