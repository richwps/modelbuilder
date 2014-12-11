package de.hsos.richwps.mb.entity;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.exception.IllegalDefaultFormatException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Datatype description for complex datatypes.
 *
 * @author dziegenh
 */
public class DataTypeDescriptionComplex implements IDataTypeDescription, Serializable, Cloneable {

    private List<ComplexDataTypeFormat> formats;

    private ComplexDataTypeFormat defaultFormat;

    public DataTypeDescriptionComplex() {
        this.formats = new LinkedList<>();
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

        DataTypeDescriptionComplex other = ((DataTypeDescriptionComplex) obj);

        boolean formatsEqual = false;
        List<ComplexDataTypeFormat> otherFormats = other.getFormats();
        if (null != otherFormats && null != formats) {
            formatsEqual = formats.containsAll(otherFormats) && otherFormats.size() == formats.size();
        }

        // equal if: both null or both not null
        boolean defaultFormatEqual = !(null == getDefaultFormat() ^ null == other.getDefaultFormat());
        // if both not null: default format must also be equal
        if (null != getDefaultFormat() && null != other.getDefaultFormat()) {
            defaultFormatEqual = getDefaultFormat().equals(other.getDefaultFormat());
        }

        return formatsEqual && defaultFormatEqual;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.formats);
        return hash;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        DataTypeDescriptionComplex clone = new DataTypeDescriptionComplex();

        for (ComplexDataTypeFormat aFormat : formats) {
            clone.formats.add(aFormat);
        }

        try {
            clone.setDefaultFormat(defaultFormat);
        } catch (IllegalDefaultFormatException ex) {
            // ignore; just log
            Logger.log("DataTypeDescriptionComplex: IllegalDefaultFormat " + ex);
        }

        return clone;
    }

}
