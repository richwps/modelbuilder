package de.hsos.richwps.mb.richWPS.entity.impl.arguments;

import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputComplexDataSpecifier;

/**
 *
 * @author dalcacer
 */
public class OutputComplexDataArgument implements IOutputArgument {

    private OutputComplexDataSpecifier specifier;

    private boolean asReference = false;
    private boolean storeOutput = false;
    private Object value;
    private Class valuedesc;
    private String encoding;
    private String schema;

    /**
     * The selected mimetype.
     */
    private String mimetype;

    public OutputComplexDataArgument(OutputComplexDataSpecifier specifier) {
        this.specifier = specifier;
    }

    public String getIdentifier() {
        return this.specifier.getIdentifier();
    }

    public void setAsReference(boolean val) {
        this.asReference = val;
    }

    public void storeOutput(boolean val) {
        this.storeOutput = val;
    }

    public OutputComplexDataSpecifier getSpecifier() {
        return specifier;
    }

    public void setSpecifier(OutputComplexDataSpecifier specifier) {
        this.specifier = specifier;
    }

    public boolean isStoreOutput() {
        return storeOutput;
    }

    public void setStoreOutput(boolean storeOutput) {
        this.storeOutput = storeOutput;
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

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public boolean isAsReference() {
        return asReference;
    }

}
