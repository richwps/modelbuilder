package de.hsos.richwps.mb.richWPS.entity.impl;

import de.hsos.richwps.mb.richWPS.entity.IInputArgument;
import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import java.util.HashMap;
import java.util.Objects;

/**
 * Represents a ExecuteRequest. The RichWPSProvider is able to perform a
 * wps:execute()-Request with this object.
 *
 * @author dalcacer
 * @see ProcessDescription
 */
public class ExecuteRequest extends ProcessDescription implements IRequest {

    
    /**
     * List of actual process inputs before execution.
     */
    private HashMap<String, IInputArgument> actualinputs;
    /**
     * List of actual, requested process outputs before execution.
     */
    private HashMap<String, IOutputArgument> actualoutputs;
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

    /**
     *
     * @return
     */
    public HashMap<String, IInputArgument> getInputArguments() {
        return this.actualinputs;
    }

    /**
     *
     * @param arguments
     */
    public void setInputArguments(HashMap<String, IInputArgument> arguments) {
        this.actualinputs = arguments;
    }

    /**
     *
     * @return
     */
    public HashMap<String, IOutputArgument> getOutputArguments() {
        return this.actualoutputs;
    }

    /**
     *
     * @param arguments
     */
    public void setOutputArguments(HashMap<String, IOutputArgument> arguments) {
        this.actualoutputs = arguments;
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
}
