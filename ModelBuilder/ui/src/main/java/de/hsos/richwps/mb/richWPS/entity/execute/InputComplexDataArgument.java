package de.hsos.richwps.mb.richWPS.entity.execute;

import de.hsos.richwps.mb.richWPS.entity.IInputArgument;
import de.hsos.richwps.mb.richWPS.entity.specifier.InputComplexDataSpecifier;

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

    public InputComplexDataArgument(InputComplexDataSpecifier specifier) {
        this.specifier = specifier;
    }

    public String getIdentifier() {
        return this.specifier.getIdentifier();
    }

    public void setMimeType(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getMimeType() {
        return this.mimetype;
    }

    public void setAsReference(boolean val) {
        this.asReference = val;
    }
    
    public String getURL(){
        return this.url;
    }
    
    public void setURL(String url){
        this.url=url;
    }

    public InputComplexDataSpecifier getSpecifier() {
        return specifier;
    }

    public void setSpecifier(InputComplexDataSpecifier specifier) {
        this.specifier = specifier;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Class getValuedesc() {
        return valuedesc;
    }

    public void setValuedesc(Class valuedesc) {
        this.valuedesc = valuedesc;
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

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }
    
    
}
