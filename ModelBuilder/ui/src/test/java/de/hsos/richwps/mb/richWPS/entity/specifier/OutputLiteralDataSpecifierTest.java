/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.richWPS.entity.specifier;

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
     * Test of getIdentifier method, of class OutputLiteralDataSpecifier.
     */
    @Test
    public void testConstruction() {
        System.out.println("testConstruction");
        OutputLiteralDataSpecifier specifier = new OutputLiteralDataSpecifier();
        specifier.setIdentifier("aabb");
        specifier.setTitle("aabb");
        specifier.setAbstract("aabb's abstract");
        specifier.setType("xs:string");
        BasicOutputDescriptionType description = specifier.toBasicOutputDescriptionType();
        
        OutputLiteralDataSpecifier specificer2 = new OutputLiteralDataSpecifier(description.getOdt());
        assertEquals(specifier, specificer2);
    }
    
}
