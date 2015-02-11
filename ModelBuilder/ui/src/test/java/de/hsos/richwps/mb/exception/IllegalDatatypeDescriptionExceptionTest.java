/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.exception;

import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionLiteral;
import de.hsos.richwps.mb.entity.ports.ComplexDataInput;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author dziegenh
 */
public class IllegalDatatypeDescriptionExceptionTest {

    public IllegalDatatypeDescriptionExceptionTest() {
    }

    @Before
    public void setUp() {
    }

    @Test(expected = IllegalDatatypeDescriptionException.class)
    public void testException() throws IllegalDatatypeDescriptionException {
        ComplexDataInput complexDataInput = new ComplexDataInput();
        DataTypeDescriptionLiteral dataTypeDescriptionLiteral = new DataTypeDescriptionLiteral("test");

        // provoke exception by setting literal description to complex datatype
        complexDataInput.setDataTypeDescription(dataTypeDescriptionLiteral);
    }

    @Test
    public void testNoException() throws IllegalDatatypeDescriptionException {
        ComplexDataInput complexDataInput = new ComplexDataInput();
        DataTypeDescriptionComplex dataTypeDescriptionComplex = new DataTypeDescriptionComplex();

        // no exception expected
        complexDataInput.setDataTypeDescription(dataTypeDescriptionComplex);
    }

}
