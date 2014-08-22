/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.entity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author dziegenh
 */
public class ProcessPortTest {

    public ProcessPortTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Test of setDataTypeDescription method, of class ProcessPort.
     */
    @Test
    public void testSetDataTypeDescription() {
        System.out.println("setDataTypeDescription");

        // test correct datatype / description combination
        IDataTypeDescription dataTypeDescription = new DataTypeDescriptionComplex(new ComplexDataTypeFormat("mime", "schema", "enc"));
        ProcessPort instance = new ProcessPort(ProcessPortDatatype.COMPLEX);
        instance.setDataTypeDescription(dataTypeDescription);

        // test wrong datatype / description combination
        exception.expect(IllegalArgumentException.class);
        instance.setDatatype(ProcessPortDatatype.LITERAL);
    }

    /**
     * Test of setDataTypeDescription method, of class ProcessPort.
     */
    @Test
    public void testSetDataTypeDescription2() {
        System.out.println("setDataTypeDescription");

        // test wrong datatype / description combination
        IDataTypeDescription dataTypeDescription = new DataTypeDescriptionComplex(new ComplexDataTypeFormat("mime", "schema", "enc"));
        ProcessPort instance = new ProcessPort(ProcessPortDatatype.LITERAL);

        exception.expect(IllegalArgumentException.class);
        instance.setDataTypeDescription(dataTypeDescription);
    }

}
