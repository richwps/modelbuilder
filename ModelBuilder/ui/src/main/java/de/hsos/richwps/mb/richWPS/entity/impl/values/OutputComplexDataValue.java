package de.hsos.richwps.mb.richWPS.entity.impl.values;

import de.hsos.richwps.mb.richWPS.entity.IOutputDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputComplexDataDescription;

/**
 *
 * @author dalcacer
 */
public class OutputComplexDataValue implements IOutputDescription {

    private OutputComplexDataDescription description;

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

    /**
     *
     * @param description
     */
    public OutputComplexDataValue(OutputComplexDataDescription description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public String getIdentifier() {
        return this.description.getIdentifier();
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
     * @param val
     */
    public void storeOutput(boolean val) {
        this.storeOutput = val;
    }

    /**
     *
     * @return
     */
    public OutputComplexDataDescription getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(OutputComplexDataDescription description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public boolean isStoreOutput() {
        return storeOutput;
    }

    /**
     *
     * @param storeOutput
     */
    public void setStoreOutput(boolean storeOutput) {
        this.storeOutput = storeOutput;
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

    /**
     *
     * @return
     */
    public boolean isAsReference() {
        return asReference;
    }

}
