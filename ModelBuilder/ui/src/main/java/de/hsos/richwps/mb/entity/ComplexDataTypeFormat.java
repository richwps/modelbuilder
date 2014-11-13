package de.hsos.richwps.mb.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Describes a specific complex datatype format.
 *
 * @author dziegenh
 */
public class ComplexDataTypeFormat implements Serializable {

    private String mimeType;
    private String schema;
    private String encoding;

    private String toolTipText;

    public ComplexDataTypeFormat() {
        this.mimeType = "";
        this.schema = "";
        this.encoding = "";
    }

    public ComplexDataTypeFormat(String mimeType, String schema, String encoding) {
        this.mimeType = mimeType;
        this.schema = schema;
        this.encoding = encoding;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
        this.toolTipText = null;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
        this.toolTipText = null;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
        this.toolTipText = null;
    }

    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    public String getToolTipText() {
        if (null == toolTipText) {
            toolTipText
                    = "<html><b>MimeType: "
                    + ComplexDataTypeFormat.getValueForViews(mimeType)
                    + "</b><br/>&nbsp;&nbsp;Schema: "
                    + ComplexDataTypeFormat.getValueForViews(schema)
                    + "<br/>&nbsp;&nbsp;Encoding: "
                    + ComplexDataTypeFormat.getValueForViews(encoding)
                    + "</html>";
        }

        return toolTipText;
    }

    public static String getValueForViews(String value) {
        return (null == value || value.isEmpty()) ? "-" : value.trim();
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof ComplexDataTypeFormat)) {
            return false;
        }

        ComplexDataTypeFormat other = (ComplexDataTypeFormat) obj;

        boolean mimeEqual = valuesEqual(mimeType, other.mimeType);
        boolean encoEqual = valuesEqual(encoding, other.encoding);
        boolean scheEqual = valuesEqual(schema, other.schema);

        return mimeEqual && encoEqual && scheEqual;
    }

    @Override
    public int hashCode() {
        int hash = 5;

        String mime = ComplexDataTypeFormat.getValueForViews(this.mimeType);
        String schem = ComplexDataTypeFormat.getValueForViews(this.schema);
        String enc = ComplexDataTypeFormat.getValueForViews(this.encoding);

        hash = 31 * hash + Objects.hashCode(mime);
        hash = 17 * hash + Objects.hashCode(schem);
        hash = 43 * hash + Objects.hashCode(enc);

        return hash;
    }

    private boolean valuesEqual(String val1, String val2) {
        if (null == val1) {
            val1 = "";
        }
        if (null == val2) {
            val2 = "";
        }

        return val1.equals(val2);
    }

}
