package de.hsos.richwps.mb.entity;

/**
 *
 * @author dziegenh
 */
public class DataTypeDescriptionLiteral implements IDataTypeDescription, Cloneable {

    private String value;

    public DataTypeDescriptionLiteral() {
    }

    public DataTypeDescriptionLiteral(String value) {
        this.value = value;
    }

    @Override
    public boolean isDescriptionFor(ProcessPortDatatype dataType) {
        return null == dataType || dataType.equals(ProcessPortDatatype.LITERAL);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (null == value) {
            return "";
        }
        return value;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        DataTypeDescriptionLiteral clone = new DataTypeDescriptionLiteral(value);
        return clone;
    }

}
