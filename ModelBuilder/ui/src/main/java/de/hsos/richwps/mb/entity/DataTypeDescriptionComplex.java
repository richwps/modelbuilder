/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.entity;

/**
 *
 * @author dziegenh
 */
public class DataTypeDescriptionComplex implements IDataTypeDescription {

    private ComplexDataTypeFormat format;

    public DataTypeDescriptionComplex() {
    }

    public DataTypeDescriptionComplex(ComplexDataTypeFormat format) {
        this.format = format;
    }

    public ComplexDataTypeFormat getFormat() {
        return format;
    }

    public void setFormat(ComplexDataTypeFormat format) {
        this.format = format;
    }

    @Override
    public boolean isDescriptionFor(ProcessPortDatatype dataType) {
        return null == dataType || dataType.equals(ProcessPortDatatype.COMPLEX);
    }

}
