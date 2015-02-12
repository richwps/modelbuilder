package de.hsos.richwps.mb.richWPS.entity.impl;

import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputBoundingBoxDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputLiteralDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputBoundingBoxDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputLiteralDataDescription;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;

/**
 * Represents a DescribeRequest. The RichWPSProvider is able to perform a
 * wps:describeProcess()-request with this object. This class can be used to
 * prepare a wps:execute()-request in combination with the according
 * ExecuteRequest-object.
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class DescribeRequest implements IRequest, Serializable {

    /**
     * The endpoint to call or discover.
     */
    protected String endpoint = "";
    /**
     * The id of the process which shall be described.
     */
    protected String identifier = "";

    /**
     * The title of the process.
     */
    protected String title = "";
    /**
     * The version of this process.
     */
    protected String processversion = "";

    /**
     * The abstract.
     */
    protected String theabstract = "";
    /**
     * List of available process inputs and their specification/types.
     */
    protected List<IInputSpecifier> availableinputs;
    /**
     * List of available process outputs and their specification/types.
     */
    protected List<IOutputSpecifier> availableoutputs;
    /**
     * Exception instead of result.
     */
    protected boolean wasException = false;
    /**
     * Exception text.
     */
    protected String exception = "";

    /**
     * The corresponding serverid.
     */
    protected String serverid = "";

    /**
     * Constructs a new ExecuteRequest.
     */
    public DescribeRequest() {
        this.endpoint = new String();
        this.identifier = new String();
        this.processversion = new String();
        this.availableinputs = new LinkedList<>();
        this.availableoutputs = new LinkedList<>();
        this.serverid = new String();
    }

    /**
     *
     * @return
     */
    @Override
    public String getEndpoint() {
        return endpoint;
    }

    /**
     *
     * @param endpoint
     */
    @Override
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     *
     * @return
     */
    @Override
    public String getServerId() {
        if (this.serverid.length() == 0) {
            return this.endpoint;
        }
        return serverid;
    }

    /**
     *
     * @return
     */
    @Override
    public String getIdentifier() {
        return identifier;
    }

    /**
     *
     * @param processid
     */
    public void setIdentifier(String processid) {
        this.identifier = processid;
    }

    /**
     *
     * @return
     */
    @Override
    public List<IInputSpecifier> getInputs() {
        return this.availableinputs;
    }

    /**
     *
     * @return
     */
    @Override
    public List<IOutputSpecifier> getOutputs() {
        return this.availableoutputs;
    }

    /**
     *
     * @return
     */
    @Override
    public String getProcessversion() {
        return this.processversion;
    }

    @Override
    public String getAbstract() {
        return theabstract;
    }

    public void setAbstract(String theabstract) {
        this.theabstract = theabstract;
    }

    /**
     *
     * @param version
     */
    public void setProcessVersion(String version) {
        this.processversion = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isException() {
        return wasException;
    }

    /**
     *
     * @return
     */
    @Override
    public String getException() {
        return exception;
    }

    /**
     *
     * @param message
     */
    @Override
    public void addException(final String message) {
        wasException = true;
        this.exception += message;
    }

    @Override
    public void flushException() {
        this.wasException = false;
        this.exception = "";
    }

    /**
     * Adds an input specification to the list of available inputs.
     *
     * @param description
     */
    public void addInput(final InputDescriptionType description) {
        if (description.getComplexData() != null) {
            IInputSpecifier aninput = new InputComplexDataDescription(description);
            this.availableinputs.add(aninput);
        } else if (description.getLiteralData() != null) {
            IInputSpecifier aninput = new InputLiteralDataDescription(description);
            this.availableinputs.add(aninput);
        } else if (description.getBoundingBoxData() != null) {
            IInputSpecifier aninput = new InputBoundingBoxDataDescription(description);
            this.availableinputs.add(aninput);
        }
    }

    /**
     * Adds an output specification to the list of available outputs.
     *
     * @param description
     */
    public void addOutput(final OutputDescriptionType description) {
        if (description.getComplexOutput() != null) {
            IOutputSpecifier anoutput = new OutputComplexDataDescription(description);
            this.availableoutputs.add(anoutput);
        } else if (description.getLiteralOutput() != null) {
            IOutputSpecifier anoutput = new OutputLiteralDataDescription(description);
            this.availableoutputs.add(anoutput);
        } else if (description.getBoundingBoxOutput() != null) {
            IOutputSpecifier anoutput = new OutputBoundingBoxDataDescription(description);
            this.availableoutputs.add(anoutput);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.endpoint);
        hash = 11 * hash + Objects.hashCode(this.identifier);
        hash = 11 * hash + Objects.hashCode(this.title);
        hash = 11 * hash + Objects.hashCode(this.processversion);
        hash = 11 * hash + Objects.hashCode(this.availableinputs);
        hash = 11 * hash + Objects.hashCode(this.availableoutputs);
        hash = 11 * hash + (this.wasException ? 1 : 0);
        hash = 11 * hash + Objects.hashCode(this.exception);
        return hash;
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DescribeRequest other = (DescribeRequest) obj;
        if (!Objects.equals(this.endpoint, other.endpoint)) {
            return false;
        }
        if (!Objects.equals(this.identifier, other.identifier)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.processversion, other.processversion)) {
            return false;
        }
        if (!Objects.equals(this.availableinputs, other.availableinputs)) {
            return false;
        }
        if (!Objects.equals(this.availableoutputs, other.availableoutputs)) {
            return false;
        }
        if (this.wasException != other.wasException) {
            return false;
        }
        if (!Objects.equals(this.exception, other.exception)) {
            return false;
        }
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "ProcessDescription{" + "endpoint=" + endpoint + ", identifier=" + identifier + ", title=" + title + ", processversion=" + processversion + ", theabstract=" + theabstract + ", availableinputs=" + availableinputs + ", availableoutputs=" + availableoutputs + ", wasException=" + wasException + ", exception=" + exception + ", serverid=" + serverid + '}';
    }

    @Override
    public void flushResults() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isLoaded() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
