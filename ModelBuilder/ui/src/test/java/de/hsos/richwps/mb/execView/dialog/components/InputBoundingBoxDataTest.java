package de.hsos.richwps.mb.execView.dialog.components;

import de.hsos.richwps.mb.app.view.dialogs.components.inputforms.InputBBoxForm;
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
     * Test of getDescription method, of class InputBBoxForm.
     */
    @Test
    public void testGetDescription() {
        System.out.println("getDescription");
        InputBoundingBoxDataDescription desc;
        desc = new InputBoundingBoxDataDescription();
        InputBBoxForm instance = new InputBBoxForm(desc);
        InputBoundingBoxDataDescription result = instance.getDescription();
        assertEquals(desc, result);
    }

    /**
     * Test of getText method, of class InputBBoxForm.
     */
    @Test
    public void testGetText() {
        System.out.println("getText");
        InputBoundingBoxDataDescription desc;
        desc = new InputBoundingBoxDataDescription();
        InputBBoxForm instance = new InputBBoxForm(desc);
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
        InputBoundingBoxDataDescription desc;
        desc = new InputBoundingBoxDataDescription();
        InputBBoxForm instance = new InputBBoxForm(desc);
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
        InputBoundingBoxDataDescription desc;
        desc = new InputBoundingBoxDataDescription();
        InputBBoxForm instance = new InputBBoxForm(desc);
        boolean expResult = false;
        boolean result = instance.isMandatory();
        assertEquals(expResult, result);
    }
    
}
