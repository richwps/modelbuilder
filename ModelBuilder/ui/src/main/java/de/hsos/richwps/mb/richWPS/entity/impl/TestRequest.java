package de.hsos.richwps.mb.richWPS.entity.impl;

import de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import java.util.ArrayList;
import java.util.List;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;
import org.n52.wps.client.richwps.ProcessDescriptionTypeBuilder;

/**
 *
 * @author dalcacer
 */
public class TestRequest extends ExecuteRequest implements IRequest {

    /**
     * The wps-t endpoint to call.
     */
    protected String endpoint;

    /**
     * The id of the process which shall be deployed.
     */
    protected String identifier;

    /**
     * The title of the process.
     */
    protected String title;

    /**
     * The processversion.
     */
    protected String processversion;

    /**
     * List of available process inputs and their specification/types.
     */
    protected List<IInputSpecifier> inputs;
    /**
     * List of available process outputs and their specification/types.
     */
    protected List<IOutputSpecifier> outputs;

    /**
     * Execution Unit.
     */
    protected String executionUnit;

    /**
     * The deploymentprofile for the execution unit.
     */
    protected String deploymentprofile;

    /**
     * The processes' abstract.
     */
    protected String theabstract;

    /**
     * A flag that indicates if the execution unit should be stored when
     * wps:undeploy() is called.
     */
    private boolean keepExecUnit;

    /**
     * Exception instead of result.
     */
    protected boolean wasException = false;
    /**
     * Exception text.
     */
    protected String exception = "";

    /**
     * The corresponding serverid (wps-endpoint).
     */
    protected String serverid = "";
    
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
     * Constructs a new DeployRequest.
     *
     * @param endpoint
     * @param identifier
     * @param title
     * @param processversion
     * @param deploymentprofile
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
    @Override
    public String getServerId() {
        //TODO assumption :(
        if (this.serverid.length() == 0) {
            String uri = this.endpoint;
            uri = uri.replace(IRichWPSProvider.DEFAULT_RICHWPS_ENDPOINT, IRichWPSProvider.DEFAULT_WPS_ENDPOINT);
            return uri;
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
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     */
    public String getProcessversion() {
        return processversion;
    }

    /**
     *
     * @param processversion
     */
    public void setProcessversion(String processversion) {
        this.processversion = processversion;
    }

    /**
     *
     * @return
     */
    @Override
    public List<IInputSpecifier> getInputs() {
        return inputs;
    }

    /**
     *
     * @param inputs
     */
    public void setInputs(List<IInputSpecifier> inputs) {
        this.inputs = inputs;
    }

    /**
     *
     * @param specifier
     */
    public void addInput(IInputSpecifier specifier) {
        this.inputs.add(specifier);
    }

    /**
     *
     * @return
     */
    @Override
    public List<IOutputSpecifier> getOutputs() {
        return outputs;
    }

    /**
     *
     * @param outputs
     */
    public void setOutputs(List<IOutputSpecifier> outputs) {
        this.outputs = outputs;
    }

    /**
     *
     * @param specifier
     */
    public void addOutput(IOutputSpecifier specifier) {
        this.outputs.add(specifier);
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
    @Override
    public String getAbstract() {
        return theabstract;
    }

    /**
     *
     * @param theabstract
     */
    public void setAbstract(String theabstract) {
        this.theabstract = theabstract;
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

    @Override
    public void flushResults() {
        //TODO nothing to flush, yet.
    }

    
      /**
     * Adds an output specification to the list of available outputs.
     *
     * @param identifier.
     */
    public void addVariable(String identifier){
        this.variables.add(identifier);
    }
    
    public List<String> getVariables(){
        return this.variables;
    }
    
    /**
     *
     * @return
     */
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
        description.setAbstract(this.theabstract);

        //Convert inputs from IInputSpecifier-list to InputDescriptionType and add them.
        for (IInputSpecifier specifier : this.inputs) {
            description.addNewInputToDataInputs(specifier.toInputDescription());
        }

        return description.getPdt();
    }
}
