package de.hsos.richwps.mb.richWPS.entity.impl;

import de.hsos.richwps.mb.richWPS.entity.IInputArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputLiteralDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputLiteralDataSpecifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;

/**
 * Represents a ExecuteRequest. The RichWPSProvider is able to perform a
 * wps:execute()-Request with this object.
 *
 * @author dalcacer
 */
public class ExecuteRequest implements IRequest {

    /**
     * The endpoint to call or discover.
     */
    private String endpoint = "";
    /**
     * The id of the process which shall be executed.
     */
    private String identifier = "";
    /**
     * The version of this process.
     */
    private String processversion = "";

    /**
     * The abstract.
     */
    private String theabstract = "";
    /**
     * List of available process inputs and their specification/types.
     */
    private List<IInputSpecifier> availableinputs;
    /**
     * List of available process outputs and their specification/types.
     */
    private List<IOutputSpecifier> availableoutputs;
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
     * Exception instead of result.
     */
    private boolean wasException = false;
    /**
     * Exception text.
     */
    private String exception = "";

    /**
     * The corresponding serverid.
     */
    private String serverid = "";

    /**
     * Constructs a new ExecuteRequest.
     */
    public ExecuteRequest() {
        this.endpoint = new String();
        this.identifier = new String();
        this.processversion = new String();
        this.availableinputs = new ArrayList<>();
        this.actualinputs = new HashMap<>();
        this.availableoutputs = new ArrayList<>();
        this.actualoutputs = new HashMap<>();
        this.results = new HashMap<>();
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
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     *
     * @return
     */
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

    /**
     * Adds an input specification to the list of available inputs.
     *
     * @param description
     */
    public void addInput(final InputDescriptionType description) {
        if (description.getComplexData() != null) {
            IInputSpecifier aninput = new InputComplexDataSpecifier(description);
            this.availableinputs.add(aninput);
        } else if (description.getLiteralData() != null) {
            IInputSpecifier aninput = new InputLiteralDataSpecifier(description);
            this.availableinputs.add(aninput);
        } else if (description.getBoundingBoxData() != null) {
            //FIXME
        }
    }

    /**
     * Adds an output specification to the list of available outputs.
     *
     * @param description
     */
    public void addOutput(final OutputDescriptionType description) {
        if (description.getComplexOutput() != null) {
            IOutputSpecifier anoutput = new OutputComplexDataSpecifier(description);
            this.availableoutputs.add(anoutput);
        } else if (description.getLiteralOutput() != null) {
            IOutputSpecifier anoutput = new OutputLiteralDataSpecifier(description);
            this.availableoutputs.add(anoutput);
        } else if (description.getBoundingBoxOutput() != null) {
            //FIXME 
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
