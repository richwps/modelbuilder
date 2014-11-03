/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.execView.dialog.components;

import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputBoundingBoxDataSpecifier;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author carstenduvel
 */
public class InputBoundingBoxDataTest {
    
    public InputBoundingBoxDataTest() {
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
     * Test of getSpecifier method, of class InputBoundingBoxData.
     */
    @Test
    public void testGetSpecifier() {
        System.out.println("getSpecifier");
        InputBoundingBoxDataSpecifier spec;
        spec = new InputBoundingBoxDataSpecifier();
        InputBoundingBoxData instance = new InputBoundingBoxData(spec);
        InputBoundingBoxDataSpecifier result = instance.getSpecifier();
        assertEquals(spec, result);
    }

    /**
     * Test of getText method, of class InputBoundingBoxData.
     */
    @Test
    public void testGetText() {
        System.out.println("getText");
        InputBoundingBoxDataSpecifier spec;
        spec = new InputBoundingBoxDataSpecifier();
        InputBoundingBoxData instance = new InputBoundingBoxData(spec);
        String expResult = "1.0"+" "+"1.0"+","+"0.0"+" "+"0.0";
        String result = instance.getText();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCRS method, of class InputBoundingBoxData.
     */
    @Test
    public void testGetCRS() {
        System.out.println("getCRS");
        InputBoundingBoxDataSpecifier spec;
        spec = new InputBoundingBoxDataSpecifier();
        InputBoundingBoxData instance = new InputBoundingBoxData(spec);
        String expResult = "";
        String result = instance.getCRS();
        assertEquals(expResult, result);
    }

    /**
     * Test of isMandatory method, of class InputBoundingBoxData.
     */
    @Test
    public void testIsMandatory() {
        System.out.println("isMandatory");
        InputBoundingBoxDataSpecifier spec;
        spec = new InputBoundingBoxDataSpecifier();
        InputBoundingBoxData instance = new InputBoundingBoxData(spec);
        boolean expResult = false;
        boolean result = instance.isMandatory();
        assertEquals(expResult, result);
    }
    
}
