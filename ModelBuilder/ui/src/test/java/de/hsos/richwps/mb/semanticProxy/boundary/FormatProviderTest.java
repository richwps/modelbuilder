/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.semanticProxy.boundary;

import de.hsos.richwps.mb.processProvider.boundary.FormatProvider;
import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import java.util.List;
import junit.framework.TestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author dziegenh
 */
public class FormatProviderTest extends TestCase {

    public FormatProviderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getInstance method, of class FormatProvider.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        FormatProvider result = FormatProvider.getInstance();
        assertNotNull(result);
    }

    /**
     * Test of getComplexDataTypes method, of class FormatProvider.
     */
    @Test
    public void testGetComplexDataTypes() throws Exception {
        System.out.println("getComplexDataTypes");
        FormatProvider instance = FormatProvider.getInstance();
        List<ComplexDataTypeFormat> result = instance.getComplexDataTypes();
        assertFalse(result.isEmpty());
    }

    /**
     * Test of getDefaultDataType method, of class FormatProvider.
     */
    @Test
    public void testGetDefaultDataType() {
        System.out.println("getDefaultDataType");
        FormatProvider instance = FormatProvider.getInstance();
        ComplexDataTypeFormat result = instance.getDefaultDataType();
        assertNotNull(result);
    }

}
