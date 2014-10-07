package de.hsos.richwps.mb.entity;

import java.io.Serializable;

/**
 * Datatype description for complex datatypes.
 *
 * @TODO add mutliple formats and set default format.
 *
 * @author dziegenh
 */
public class DataTypeDescriptionComplex implements IDataTypeDescription, Serializable {

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
