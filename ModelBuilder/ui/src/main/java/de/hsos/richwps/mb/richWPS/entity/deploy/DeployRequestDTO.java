package de.hsos.richwps.mb.richWPS.entity.deploy;

import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import java.util.ArrayList;
import java.util.List;
import org.n52.wps.client.transactional.BasicProcessDescriptionType;

/**
 *
 * @author dalcacer
 */
public class DeployRequestDTO {

    BasicProcessDescriptionType processdescription;
    /**
     * The endpoint to call or discover.
     */
    private String endpoint;

    /**
     * The id of the process which shall be deployed.
     */
    private String identifier;

    private String title;

    private String processversion;
    /**
     * List of available process inputs and their specification/types.
     */
    private List<IInputSpecifier> inputs;
    /**
     * List of available process outputs and their specification/types.
     */
    private List<IOutputSpecifier> outputs;

    /**
     * Execution Unit.
     */
    private String executionUnit;

    public DeployRequestDTO() {
        this.endpoint = "";
        this.identifier = "";
        this.title = "";
        this.processversion = "";
        this.executionUnit = "";
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }

    public DeployRequestDTO(final String endpoint, final String identifier, final String title, final String processversion) {
        this.endpoint = endpoint;
        this.identifier = identifier;
        this.title = title;
        this.processversion = processversion;
        this.executionUnit = "";
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();

    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProcessversion() {
        return processversion;
    }

    public void setProcessversion(String processversion) {
        this.processversion = processversion;
    }

    public List<IInputSpecifier> getInputs() {
        return inputs;
    }

    public void setInputs(List<IInputSpecifier> inputs) {
        this.inputs = inputs;
    }

    public List<IOutputSpecifier> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<IOutputSpecifier> outputs) {
        this.outputs = outputs;
    }

    public void setExecutionUnit(String eu) {
        this.executionUnit = eu;
    }

    public String getExecutionUnit() {
        return this.executionUnit;
    }

}
