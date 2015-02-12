package de.hsos.richwps.mb.execView.dialog.components;

import de.hsos.richwps.mb.ui.dialogs.components.inputforms.InputBBoxForm;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputBoundingBoxDataDescription;
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
     * Test of getSpecifier method, of class InputBBoxForm.
     */
    @Test
    public void testGetSpecifier() {
        System.out.println("getSpecifier");
        InputBoundingBoxDataDescription spec;
        spec = new InputBoundingBoxDataDescription();
        InputBBoxForm instance = new InputBBoxForm(spec);
        InputBoundingBoxDataDescription result = instance.getSpecifier();
        assertEquals(spec, result);
    }

    /**
     * Test of getText method, of class InputBBoxForm.
     */
    @Test
    public void testGetText() {
        System.out.println("getText");
        InputBoundingBoxDataDescription spec;
        spec = new InputBoundingBoxDataDescription();
        InputBBoxForm instance = new InputBBoxForm(spec);
        String expResult = "-2.0"+" "+"-1.0"+","+"3.0"+" "+"4.0";
        String result = instance.getText();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCRS method, of class InputBBoxForm.
     */
    @Test
    public void testGetCRS() {
        System.out.println("getCRS");
        InputBoundingBoxDataDescription spec;
        spec = new InputBoundingBoxDataDescription();
        InputBBoxForm instance = new InputBBoxForm(spec);
        String expResult = "";
        String result = instance.getCRS();
        assertEquals(expResult, result);
    }

    /**
     * Test of isMandatory method, of class InputBBoxForm.
     */
    @Test
    public void testIsMandatory() {
        System.out.println("isMandatory");
        InputBoundingBoxDataDescription spec;
        spec = new InputBoundingBoxDataDescription();
        InputBBoxForm instance = new InputBBoxForm(spec);
        boolean expResult = false;
        boolean result = instance.isMandatory();
        assertEquals(expResult, result);
    }
    
}
