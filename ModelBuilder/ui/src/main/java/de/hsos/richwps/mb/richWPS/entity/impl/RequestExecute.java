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
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;

/**
 * An object used to parameterize and prepare execute requests.
 *
 * @author dalcacer
 */
public class RequestExecute implements IRequest {

    /**
     * The endpoint to call or discover.
     */
    private String endpoint = "";
    /**
     * The id of the process which shall be executed.
     */
    private String identifier = "";
    /**
     * The versio nof this process.
     */
    private String processversion="";
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
     * Builds a new, blank, wps:execute()-request.
     */
    public RequestExecute() {
        this.endpoint = new String();
        this.identifier = new String();
        this.processversion=new String();
        this.availableinputs = new ArrayList<>();
        this.actualinputs = new HashMap<>();
        this.availableoutputs = new ArrayList<>();
        this.actualoutputs = new HashMap<>();
        this.results = new HashMap<>();
    }
    
    
    @Override
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String processid) {
        this.identifier = processid;
    }

    @Override
    public List<IInputSpecifier> getInputs() {
        return this.availableinputs;
    }

    @Override
    public List<IOutputSpecifier> getOutputs() {
        return this.availableoutputs;
    }
    
    @Override    
    public String getProcessversion(){
        return this.processversion;
    }
    
    public void setProcessVersion(String version){
        this.processversion=version;
    }

    public HashMap<String, IInputArgument> getInputArguments() {
        return this.actualinputs;
    }

    public void setInputArguments(HashMap<String, IInputArgument> arguments) {
        this.actualinputs = arguments;
    }

    public HashMap<String, IOutputArgument> getOutputArguments() {
        return this.actualoutputs;
    }

    public void setOutputArguments(HashMap<String, IOutputArgument> arguments) {
        this.actualoutputs = arguments;
    }

    @Override
    public boolean isException() {
        return wasException;
    }
    @Override
    public String getException() {
        return exception;
    }

    @Override
    public void addException(final String message) {
        wasException = true;
        this.exception = message;
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

    public HashMap<String, Object> getResults() {
        return results;
    }

    public void setResults(HashMap<String, Object> results) {
        this.results = results;
    }

    public void addResult(final String key, final Object value) {
        this.results.put(key, value);
    }
}
