package de.hsos.richwps.mb.richWPS.entity.impl;

import de.hsos.richwps.mb.richWPS.entity.IInputValue;
import de.hsos.richwps.mb.richWPS.entity.IInputDescription;
import de.hsos.richwps.mb.richWPS.entity.IOutputDescription;
import de.hsos.richwps.mb.richWPS.entity.IOutputValue;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputBoundingBoxDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputLiteralDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputBoundingBoxDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputLiteralDataDescription;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;

/**
 * Represents a ExecuteRequest. The RichWPSProvider is able to perform a
 * wps:execute()-Request with this object.
 *
 * @author dalcacer
 * @see DescribeRequest
 */
public class ExecuteRequest extends DescribeRequest implements IRequest, Serializable {

    static final long serialVersionUID = 42L;
    /**
     * List of actual process inputs before execution.
     */
    private HashMap<String, IInputValue> actualinputs;
    /**
     * List of actual, requested process outputs before execution.
     */
    private HashMap<String, IOutputDescription> actualoutputs;
    /**
     * The actual results.
     */
    private HashMap<String, Object> results;

    /**
     * Constructs a new ExecuteRequest.
     */
    public ExecuteRequest() {
        super();
        this.actualinputs = new HashMap<>();
        this.actualoutputs = new HashMap<>();
        this.results = new HashMap<>();
    }

    public ExecuteRequest(DescribeRequest req) {
        super();
        this.endpoint = req.getEndpoint();
        this.exception = req.getException();
        this.identifier = req.getIdentifier();
        this.processversion = req.getProcessversion();
        this.serverid = req.getServerId();
        this.theabstract = req.getAbstract();
        this.title = req.getTitle();
        this.availableinputs = req.getInputs();
        this.availableoutputs = req.getOutputs();
        this.actualinputs = new HashMap<>();
        this.actualoutputs = new HashMap<>();
        this.results = new HashMap<>();

    }

    /**
     *
     * @return
     */
    public HashMap<String, IInputValue> getInputValues() {
        return this.actualinputs;
    }

    /**
     *
     * @param values
     */
    public void setInputValues(HashMap<String, IInputValue> values) {
        this.actualinputs = values;
    }

    /**
     *
     * @return
     */
    public HashMap<String, IOutputDescription> getOutputValues() {
        return this.actualoutputs;
    }

    /**
     *
     * @param values
     */
    public void setOutputValues(HashMap<String, IOutputDescription> values) {
        this.actualoutputs = values;
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
    @Override
    public void addInput(final InputDescriptionType description) {
        if (description.getComplexData() != null) {
            IInputDescription aninput = new InputComplexDataDescription(description);
            this.availableinputs.add(aninput);
        } else if (description.getLiteralData() != null) {
            IInputDescription aninput = new InputLiteralDataDescription(description);
            this.availableinputs.add(aninput);
        } else if (description.getBoundingBoxData() != null) {
            IInputDescription aninput = new InputBoundingBoxDataDescription(description);
            this.availableinputs.add(aninput);
        }
    }

    /**
     * Adds an output specification to the list of available outputs.
     *
     * @param description
     */
    @Override
    public void addOutput(final OutputDescriptionType description) {
        if (description.getComplexOutput() != null) {
            IOutputValue anoutput = new OutputComplexDataDescription(description);
            this.availableoutputs.add(anoutput);
        } else if (description.getLiteralOutput() != null) {
            IOutputValue anoutput = new OutputLiteralDataDescription(description);
            this.availableoutputs.add(anoutput);
        } else if (description.getBoundingBoxOutput() != null) {
            IOutputValue anoutput = new OutputBoundingBoxDataDescription(description);
            this.availableoutputs.add(anoutput);
        }
    }

    /**
     *
     * @return
     */
    public HashMap<String, Object> getResults() {
        return results;
    }

    /**
     *
     * @param results
     */
    public void setResults(HashMap<String, Object> results) {
        this.results = results;
    }

    /**
     *
     * @param key
     * @param value
     */
    public void addResult(final String key, final Object value) {
        this.results.put(key, value);
    }

    @Override
    public void flushResults() {
        this.results.clear();
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
        hash = 11 * hash + Objects.hashCode(this.processversion);
        hash = 11 * hash + Objects.hashCode(this.availableinputs);
        hash = 11 * hash + Objects.hashCode(this.availableoutputs);
        hash = 11 * hash + Objects.hashCode(this.actualinputs);
        hash = 11 * hash + Objects.hashCode(this.actualoutputs);
        hash = 11 * hash + Objects.hashCode(this.results);
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
        final ExecuteRequest other = (ExecuteRequest) obj;
        if (!Objects.equals(this.endpoint, other.endpoint)) {
            return false;
        }
        if (!Objects.equals(this.identifier, other.identifier)) {
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
        if (!Objects.equals(this.actualinputs, other.actualinputs)) {
            return false;
        }
        if (!Objects.equals(this.actualoutputs, other.actualoutputs)) {
            return false;
        }
        if (!Objects.equals(this.results, other.results)) {
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
        return "RequestExecute{" + "endpoint=" + endpoint + ", identifier=" + identifier + ", processversion=" + processversion + ", availableinputs=" + availableinputs + ", availableoutputs=" + availableoutputs + ", actualinputs=" + actualinputs + ", actualoutputs=" + actualoutputs + ", results=" + results + ", wasException=" + wasException + ", exception=" + exception + '}';
    }

    @Override
    public boolean isLoaded() {
        if ((this.actualinputs.isEmpty()) && (this.actualoutputs.isEmpty())) {
            return false;
        }
        return true;
    }
    
    public boolean isDescribed() {
        if ((this.availableinputs.isEmpty()) && (this.availableoutputs.isEmpty())) {
            return false;
        }
        return true;
    }
    
    
    
}
