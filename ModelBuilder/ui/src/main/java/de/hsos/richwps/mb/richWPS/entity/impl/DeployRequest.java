package de.hsos.richwps.mb.richWPS.entity.impl;

import de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IInputDescription;
import de.hsos.richwps.mb.richWPS.entity.IOutputValue;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;
import org.n52.wps.client.richwps.ProcessDescriptionTypeBuilder;

/**
 * Represents a DeployRequest. The RichWPSProvider is able to perform a
 * wpst:deploy()-Request with this object.
 *
 * @author dalcacer
 * @verson 0.0.1s
 */
public class DeployRequest implements IRequest {

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

    /**
     * Constructs a new DeployRequest
     */
    public DeployRequest() {
        this.endpoint = "";
        this.identifier = "";
        this.title = "";
        this.processversion = "";
        this.executionUnit = "";
        this.deploymentprofile = "";
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.theabstract = "";
        this.keepExecUnit = false;
    }

    /**
     * Constructs a new DeployRequest.
     *
     * @param serverid serverid used to identify the remote.
     * @param endpoint actual endpoint (may differ from serverid).
     * @param identifier the process identifier.
     * @param title the process stitle.
     * @param processversion the process version.
     * @param deploymentprofile the deploymentprofile (ROLA).
     */
    public DeployRequest(final String serverid, final String endpoint, final String identifier,
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
     * @deprecated
     */
    public DeployRequest(final String endpoint, final String identifier,
            final String title, final String processversion, final String deploymentprofile) {
        this.endpoint = endpoint;
        this.identifier = identifier;
        this.title = title;
        this.processversion = processversion;
        this.deploymentprofile = deploymentprofile;

        this.executionUnit = "";
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
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

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.endpoint);
        hash = 59 * hash + Objects.hashCode(this.identifier);
        hash = 59 * hash + Objects.hashCode(this.title);
        hash = 59 * hash + Objects.hashCode(this.processversion);
        hash = 59 * hash + Objects.hashCode(this.inputs);
        hash = 59 * hash + Objects.hashCode(this.outputs);
        hash = 59 * hash + Objects.hashCode(this.executionUnit);
        hash = 59 * hash + Objects.hashCode(this.deploymentprofile);
        hash = 59 * hash + Objects.hashCode(this.theabstract);
        hash = 59 * hash + Objects.hashCode(this.keepExecUnit);
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
        final DeployRequest other = (DeployRequest) obj;
        if (!Objects.equals(this.identifier, other.identifier)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.processversion, other.processversion)) {
            return false;
        }
        if (!Objects.equals(this.inputs, other.inputs)) {
            return false;
        }
        if (!Objects.equals(this.outputs, other.outputs)) {
            return false;
        }
        if (!Objects.equals(this.executionUnit, other.executionUnit)) {
            return false;
        }
        if (!Objects.equals(this.deploymentprofile, other.deploymentprofile)) {
            return false;
        }
        if (!Objects.equals(this.theabstract, other.theabstract)) {
            return false;
        }
        if (this.keepExecUnit != other.keepExecUnit) {
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
        return "DeployRequest{" + "endpoint=" + endpoint + ", identifier=" + identifier + ", title=" + title + ", processversion=" + processversion + ", inputs=" + inputs + ", outputs=" + outputs + ", executionUnit=" + executionUnit + ", deploymentprofile=" + deploymentprofile + ", theabstract=" + theabstract + ", keepExecUnit=" + keepExecUnit + '}';
    }

    @Override
    public boolean isLoaded() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
