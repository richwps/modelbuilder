package de.hsos.richwps.mb.entity;

import java.util.Objects;

/**
 * A key which identifies a processes' ports. E.g. used as a HashMap key.
 *
 * @author dziegenh
 */
public class ProcessPortKey {

    private String owsIdentifier;

    private ProcessPortDatatype datatype;

    private boolean input;

    public ProcessPortKey(String owsIdentifier, ProcessPortDatatype datatype, boolean input) {
        this.owsIdentifier = owsIdentifier;
        this.datatype = datatype;
        this.input = input;
    }

    public ProcessPortKey() {
    }

    public String getOwsIdentifier() {
        return owsIdentifier;
    }

    public void setOwsIdentifier(String owsIdentifier) {
        this.owsIdentifier = owsIdentifier;
    }

    public ProcessPortDatatype getDatatype() {
        return datatype;
    }

    public void setDatatype(ProcessPortDatatype datatype) {
        this.datatype = datatype;
    }

    public boolean isInput() {
        return input;
    }

    public void setInput(boolean input) {
        this.input = input;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof ProcessPortKey)) {
            return false;
        }

        ProcessPortKey other = (ProcessPortKey) obj;

        return this.input == other.input
                && this.datatype.equals(other.datatype)
                && this.owsIdentifier.equals(other.owsIdentifier);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.owsIdentifier);
        hash = 79 * hash + Objects.hashCode(this.datatype);
        hash = 79 * hash + (this.input ? 1 : 0);
        return hash;
    }
    
}
