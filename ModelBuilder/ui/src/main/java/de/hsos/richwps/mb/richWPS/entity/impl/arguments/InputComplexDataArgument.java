package de.hsos.richwps.mb.richWPS.entity.impl.arguments;

import de.hsos.richwps.mb.richWPS.entity.IInputArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputComplexDataSpecifier;

/**
 *
 * @author dalcacer
 */
public class InputComplexDataArgument implements IInputArgument {

    private InputComplexDataSpecifier specifier;

    private boolean asReference = false;

    private String url = "";

    private Object value;

    private Class valuedesc;

    private String schema;

    private String encoding;

    /**
     * The selected mimetype.
     */
    private String mimetype;

    /**
     *
     * @param specifier
     */
    public InputComplexDataArgument(InputComplexDataSpecifier specifier) {
        this.specifier = specifier;
    }

    /**
     *
     * @return
     */
    public String getIdentifier() {
        return this.specifier.getIdentifier();
    }

    /**
     *
     * @param mimetype
     */
    public void setMimeType(String mimetype) {
        this.mimetype = mimetype;
    }

    /**
     *
     * @return
     */
    public String getMimeType() {
        return this.mimetype;
    }

    /**
     *
     * @param val
     */
    public void setAsReference(boolean val) {
        this.asReference = val;
    }

    /**
     *
     * @return
     */
    public String getURL() {
        return this.url;
    }

    /**
     *
     * @param url
     */
    public void setURL(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     */
    public InputComplexDataSpecifier getSpecifier() {
        return specifier;
    }

    /**
     *
     * @param specifier
     */
    public void setSpecifier(InputComplexDataSpecifier specifier) {
        this.specifier = specifier;
    }

    /**
     *
     * @return
     */
    public Object getValue() {
        return value;
    }

    /**
     *
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     *
     * @return
     */
    public Class getValuedesc() {
        return valuedesc;
    }

    /**
     *
     * @param valuedesc
     */
    public void setValuedesc(Class valuedesc) {
        this.valuedesc = valuedesc;
    }

    /**
     *
     * @return
     */
    public String getSchema() {
        return schema;
    }

    /**
     *
     * @param schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     *
     * @return
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     *
     * @param encoding
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     *
     * @return
     */
    public String getMimetype() {
        return mimetype;
    }

    /**
     *
     * @param mimetype
     */
    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

}
