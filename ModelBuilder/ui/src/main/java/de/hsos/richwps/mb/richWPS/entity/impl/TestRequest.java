package de.hsos.richwps.mb.richWPS.entity.impl;

import de.hsos.richwps.mb.richWPS.entity.IInputDescription;
import de.hsos.richwps.mb.richWPS.entity.IOutputValue;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import java.util.ArrayList;
import java.util.List;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;
import org.n52.wps.client.richwps.ProcessDescriptionTypeBuilder;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class TestRequest extends ExecuteRequest implements IRequest {

    /**
     * List of available process inputs and their specification/types.
     */
    protected List<IInputDescription> inputs;
    /**
     * List of available process outputs and their specification/types.
     */
    protected List<IOutputValue> outputs;

    /**
     * Execution Unit.
     */
    protected String executionUnit;

    /**
     * The deploymentprofile for the execution unit.
     */
    protected String deploymentprofile;

    /**
     * A flag that indicates if the execution unit should be stored when
     * wps:undeploy() is called.
     */
    private boolean keepExecUnit;

    protected List<String> variables;

    /**
     * Constructs a new TestRequest
     */
    public TestRequest() {
        super();
        this.endpoint = "";
        this.identifier = "";
        this.title = "";
        this.processversion = "";
        this.executionUnit = "";
        this.deploymentprofile = "";
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.variables = new ArrayList<>();
        this.theabstract = "";
        this.keepExecUnit = false;
    }

    /**
     * Constructs a new TestRequest.
     *
     * @param serverid serverid used to identify the remote.
     * @param endpoint actual endpoint (may differ from serverid).
     * @param identifier the process identifier.
     * @param title the process stitle.
     * @param processversion the process version.
     * @param deploymentprofile the deploymentprofile (ROLA).
     */
    public TestRequest(final String serverid, final String endpoint, final String identifier,
            final String title, final String processversion, final String deploymentprofile) {
        this.serverid = serverid;
        this.endpoint = endpoint;
        this.identifier = identifier;
        this.title = title;
        this.processversion = processversion;
        this.deploymentprofile = deploymentprofile;

        this.executionUnit = "";
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.variables = new ArrayList<>();
        this.theabstract = "";
        this.keepExecUnit = false;
    }

    /**
     *
     * @param execunit
     */
    public void setExecutionUnit(String execunit) {
        this.executionUnit = execunit;
    }

    /**
     *
     * @return
     */
    public String getExecutionUnit() {
        return this.executionUnit;
    }

    /**
     *
     * @return
     */
    public String getDeploymentprofile() {
        return deploymentprofile;
    }

    /**
     *
     * @param deploymentprofile
     */
    public void setDeploymentprofile(String deploymentprofile) {
        this.deploymentprofile = deploymentprofile;
    }

    /**
     *
     * @return
     */
    public boolean isKeepExecUnit() {
        return keepExecUnit;
    }

    /**
     *
     * @param keepExecUnit
     */
    public void setKeepExecUnit(boolean keepExecUnit) {
        this.keepExecUnit = keepExecUnit;
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

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }

    /**
     * Adds an output specification to the list of available intermediate
     * results.
     *
     * @param identifier.
     */
    public void addVariable(String identifier) {
        this.variables.add(identifier);
    }

    public List<String> getVariables() {
        return this.variables;
    }

    /**
     *
     * @return
     */
    @Override
    public List<IInputDescription> getInputs() {
        return inputs;
    }

    /**
     *
     * @param inputs
     */
    public void setInputs(List<IInputDescription> inputs) {
        this.inputs = inputs;
    }

    /**
     *
     * @param description
     */
    public void addInput(IInputDescription description) {
        this.inputs.add(description);
    }
    
     /**
     *
     * @return
     */
    @Override
    public List<IOutputValue> getOutputs() {
        return outputs;
    }

    /**
     *
     * @param outputs
     */
    public void setOutputs(List<IOutputValue> outputs) {
        this.outputs = outputs;
    }

    /**
     *
     * @param value
     */
    public void addOutput(IOutputValue value) {
        this.outputs.add(value);
    }


    /**
     * Creates a processdescription, which is necessary for deployment.
     *
     * @return ogc:pocessdescriptiontype
     */
    public ProcessDescriptionType toProcessDescriptionType() {
        ProcessDescriptionTypeBuilder description;
        //Convert outputs from IOutputValue-list to OutputDescriptionType-array.
        ProcessDescriptionType.ProcessOutputs ogcoutputs = ProcessDescriptionType.ProcessOutputs.Factory.newInstance();
        OutputDescriptionType[] outputarray = new OutputDescriptionType[this.outputs.size()];
        int i = 0;
        for (IOutputValue value : this.outputs) {
            OutputDescriptionType atype = value.toOutputDescription();
            outputarray[i++] = atype;
        }
        ogcoutputs.setOutputArray(outputarray);
        description = new ProcessDescriptionTypeBuilder(this.identifier, this.title, this.processversion, ogcoutputs);
        description.setAbstract(this.theabstract);

        //Convert inputs from IInputDescription-list to InputDescriptionType and add them.
        for (IInputDescription desc : this.inputs) {
            description.addNewInputToDataInputs(desc.toInputDescription());
        }

        return description.getPdt();
    }
}
