package de.hsos.richwps.mb.richWPS.entity.impl;

import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class GetProcessesRequest implements IRequest {

    private String endpoint = "";
    /**
     * The id of the process which shall be undeployed.
     */
    private String identifier = "";
    /**
     * Exception instead of result.
     */
    private boolean wasException = false;
    /**
     * Exception text.
     */
    private String exception = "";

    /**
     *
     */
    private List<String> processes;

    /**
     * The corresponding serverid (wps-endpoint).
     */
    private String serverid = "";

    public GetProcessesRequest(final String wpsurl) {
        this.endpoint = wpsurl;
        this.serverid = wpsurl;
        this.processes = new LinkedList<>();
    }

    @Override
    public String getEndpoint() {
        return this.endpoint;
    }

    @Override
    public String getIdentifier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getProcessversion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getAbstract() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<IInputSpecifier> getInputs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<IOutputSpecifier> getOutputs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isException() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getException() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addException(String message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void flushException() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void flushResults() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getServerId() {
        return this.serverid;
    }

    @Override
    public boolean isLoaded() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEndpoint(String end) {
        this.endpoint = end;
    }

    @Override
    public void setIdentifier(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<String> getProcesses() {
        return processes;
    }

    public void setProcesses(List<String> processes) {
        this.processes = processes;
    }

}
