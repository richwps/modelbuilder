package de.hsos.richwps.mb.richWPS.entity.deploy;

import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import java.util.ArrayList;
import java.util.List;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;
import org.n52.wps.client.transactional.ProcessDescriptionTypeBuilder;

/**
 *
 * @author dalcacer
 */
public class DeployRequestDTO {

    ProcessDescriptionTypeBuilder processdescription;
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

    public void addInput(IInputSpecifier specifier){
        this.inputs.add(specifier);
    }
    
    public List<IOutputSpecifier> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<IOutputSpecifier> outputs) {
        this.outputs = outputs;
    }
    
    public void addOutput(IOutputSpecifier specifier){
        this.outputs.add(specifier);
    }

    public void setExecutionUnit(String execunit) {
        this.executionUnit = execunit;
    }

    public String getExecutionUnit() {
        return this.executionUnit;
    }

    public ProcessDescriptionType toProcessDescriptionType() {
        ProcessDescriptionTypeBuilder description;
        //Convert outputs from IOutputSpecifier-list to OutputDescriptionType-array.
        ProcessDescriptionType.ProcessOutputs ogcoutputs = ProcessDescriptionType.ProcessOutputs.Factory.newInstance();
        OutputDescriptionType[] outputarray = new OutputDescriptionType[this.outputs.size()];
        int i = 0;
        for (IOutputSpecifier specifier : this.outputs) {
            OutputDescriptionType atype = specifier.toOutputDescription();
            outputarray[i++] = atype;
        }
        ogcoutputs.setOutputArray(outputarray);
        description = new ProcessDescriptionTypeBuilder(this.identifier, this.title, this.processversion, ogcoutputs);

        //Convert inputs from IInputSpecifier-list to InputDescriptionType and add them.
        for (IInputSpecifier specifier : this.inputs) {
            description.addNewInputToDataInputs(specifier.toInputDescription());
        }

        return description.getPdt();
    }

}
