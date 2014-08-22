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
public class ComplexDataTypeFormat {

    private String mimeType;
    private String schema;
    private String encoding;

    private String toolTipText;

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
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getToolTipText() {
        if (null == toolTipText) {
            toolTipText
                    = "<html>MimeType: "
                    + getValueForToolTip(mimeType)
                    + "<br/>Schema: "
                    + getValueForToolTip(schema)
                    + "<br/>Encoding: "
                    + getValueForToolTip(encoding)
                    + "</html>";
        }

        return toolTipText;
    }

    private String getValueForToolTip(String value) {
        return (null == value) ? "-" : value;
    }

    @Override
    public String toString() {
        // TODO just tmp!!
        return getToolTipText();
    }

}
