/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.properties;

import java.util.Collection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dziegenh
 */
public class PropertyTest {
    
    public PropertyTest() {
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
     * Test of clone method, of class Property.
     */
    @Test
    public void testClone() {
        System.out.println("clone");
        
        final String propertyName = "PROPERTY";
        final String propertyValueName = "PROPERTY_VALUE";
        final String propertyValueStringValue = "PROPERTY_VALUE_STRING";
        
        Property<Property> instance = new Property(propertyName);
        Property<String> propertyValue = new Property(propertyValueName);
        propertyValue.setValue(propertyValueStringValue);
        instance.setValue(propertyValue);
        
        Property<Property> result = instance.clone();
        
        assertNotNull(result);
        assertNotNull(result.getValue());
        assertNotNull(result.getValue().getValue());
        
        assertEquals(instance, result);
        assertEquals(propertyValue, result.getValue());
        assertEquals(propertyValue.getValue(), result.getValue().getValue());
    }
    
}
