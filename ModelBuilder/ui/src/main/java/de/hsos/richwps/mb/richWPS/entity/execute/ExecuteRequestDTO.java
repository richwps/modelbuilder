package de.hsos.richwps.mb.richWPS.entity.execute;

import de.hsos.richwps.mb.richWPS.entity.IInputArgument;
import de.hsos.richwps.mb.richWPS.entity.specifier.InputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.specifier.OutputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.specifier.InputLiteralDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.specifier.OutputLiteralDataSpecifier;
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
public class ExecuteRequestDTO {

    /**
     * The endpoint to call or discover.
     */
    private String endpoint;
    /**
     * The id of the process which shall be executed.
     */
    private String processid;
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
    private boolean wasException=false;
    /**
     * Exception text.
     */
    private String exception="";

    /**
     * Builds a new, blank, data transfer object.
     */
    public ExecuteRequestDTO() {
        this.endpoint = new String();
        this.processid = new String();
        this.availableinputs = new ArrayList<>();
        this.actualinputs = new HashMap<>();
        this.availableoutputs = new ArrayList<>();
        this.actualoutputs = new HashMap<>();
        this.results = new HashMap<>();
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getProcessid() {
        return processid;
    }

    public void setProcessid(String processid) {
        this.processid = processid;
    }

    public List<IInputSpecifier> getInputSpecifier() {
        return this.availableinputs;
    }

    public HashMap<String, IInputArgument> getInputArguments() {
        return this.actualinputs;
    }

    public void setInputArguments(HashMap<String, IInputArgument> arguments) {
        this.actualinputs = arguments;
    }

    public List<IOutputSpecifier> getOutputSepcifier() {
        return this.availableoutputs;
    }

    public HashMap<String, IOutputArgument> getOutputArguments() {
        return this.actualoutputs;
    }

    public void setOutputArguments(HashMap<String, IOutputArgument> arguments) {
        this.actualoutputs = arguments;
    }

    public boolean isException() {
        return wasException;
    }

    
    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
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
    
    public void addException(final String message){
        wasException=true;
        this.exception=message;
    }
}
