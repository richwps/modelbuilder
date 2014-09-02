package de.hsos.richwps.mb.richWPS.entity.impl;

import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;
import org.n52.wps.client.transactional.ProcessDescriptionTypeBuilder;

/**
 *
 * @author dalcacer
 */
public class RequestDeploy implements IRequest {

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

    private String deploymentprofile;

    private String theabstract;

    private boolean keepExecUnit;

    /**
     * Exception instead of result.
     */
    private boolean wasException = false;
    /**
     * Exception text.
     */
    private String exception = "";

    public RequestDeploy() {
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

    public RequestDeploy(final String endpoint, final String identifier,
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

    @Override
    public List<IInputSpecifier> getInputs() {
        return inputs;
    }

    public void setInputs(List<IInputSpecifier> inputs) {
        this.inputs = inputs;
    }

    public void addInput(IInputSpecifier specifier) {
        this.inputs.add(specifier);
    }

    @Override
    public List<IOutputSpecifier> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<IOutputSpecifier> outputs) {
        this.outputs = outputs;
    }

    public void addOutput(IOutputSpecifier specifier) {
        this.outputs.add(specifier);
    }

    public void setExecutionUnit(String execunit) {
        this.executionUnit = execunit;
    }

    public String getExecutionUnit() {
        return this.executionUnit;
    }

    public String getDeploymentprofile() {
        return deploymentprofile;
    }

    public void setDeploymentprofile(String deploymentprofile) {
        this.deploymentprofile = deploymentprofile;
    }

    public String getAbstract() {
        return theabstract;
    }

    public void setAbstract(String theabstract) {
        this.theabstract = theabstract;
    }

    public boolean isKeepExecUnit() {
        return keepExecUnit;
    }

    public void setKeepExecUnit(boolean keepExecUnit) {
        this.keepExecUnit = keepExecUnit;
    }

    public boolean isException() {
        return wasException;
    }

    public String getException() {
        return exception;
    }

    public void addException(final String message){
        wasException=true;
        this.exception=message;
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
        description.setAbstract(this.theabstract);

        //Convert inputs from IInputSpecifier-list to InputDescriptionType and add them.
        for (IInputSpecifier specifier : this.inputs) {
            description.addNewInputToDataInputs(specifier.toInputDescription());
        }

        return description.getPdt();
    }

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RequestDeploy other = (RequestDeploy) obj;
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

    @Override
    public String toString() {
        return "DeployRequestDTO{" + "endpoint=" + endpoint + ", identifier=" + identifier + ", title=" + title + ", processversion=" + processversion + ", inputs=" + inputs + ", outputs=" + outputs + ", executionUnit=" + executionUnit + ", deploymentprofile=" + deploymentprofile + ", theabstract=" + theabstract + ", keepExecUnit=" + keepExecUnit + '}';
    }
}
