package de.hsos.richwps.mb.entity;

import de.hsos.richwps.mb.exception.IllegalDefaultFormatException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Datatype description for complex datatypes.
 *
 * @TODO add mutliple formats and set default format.
 *
 * @author dziegenh
 */
public class DataTypeDescriptionComplex implements IDataTypeDescription, Serializable {

    private List<ComplexDataTypeFormat> formats;

    private ComplexDataTypeFormat defaultFormat;

    public DataTypeDescriptionComplex() {
        formats = new LinkedList<>();
    }

    public DataTypeDescriptionComplex(ComplexDataTypeFormat format) {
        this.formats = new LinkedList<>();
        this.formats.add(format);
    }

    public DataTypeDescriptionComplex(List<ComplexDataTypeFormat> formats) {
        this.formats = formats;
    }

    public List<ComplexDataTypeFormat> getFormats() {
        return formats;
    }

    public void setFormats(List<ComplexDataTypeFormat> formats) {
        this.formats = formats;
    }

    public void setDefaultFormat(ComplexDataTypeFormat defaultFormat) throws IllegalDefaultFormatException {
        if (null != defaultFormat && !formats.contains(defaultFormat)) {
            throw new IllegalDefaultFormatException();
        }

        this.defaultFormat = defaultFormat;
    }

    public ComplexDataTypeFormat getDefaultFormat() {
        return defaultFormat;
    }

    @Override
    public boolean isDescriptionFor(ProcessPortDatatype dataType) {
        return null == dataType || dataType.equals(ProcessPortDatatype.COMPLEX);
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof DataTypeDescriptionComplex)) {
            return false;
        }

        List<ComplexDataTypeFormat> otherFormats = ((DataTypeDescriptionComplex) obj).getFormats();
        boolean formatsEqual = formats.containsAll(otherFormats) && otherFormats.containsAll(formats);

        return formatsEqual;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.formats);
        return hash;
    }

}
