package de.hsos.richwps.mb.richWPS.entity.impl;

import de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import java.util.List;

/**
 * Represents a UndeployRequest. The RichWPSProvider is able to perform a
 * wpst:undeploy()-Request with this object.
 *
 * @author dalcacer
 */
public class UndeployRequest implements IRequest {

    /**
     * The wps-t endpoint to call.
     */
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
     * The corresponding serverid (wps-endpoint).
     */
    private String serverid = "";

    /**
     * Constructs a new Undeploy-Request..
     * @param endpoint
     * @param identifier 
     */
    public UndeployRequest(String serverid, String endpoint, String identifier) {
        this.serverid=serverid;
        this.endpoint = endpoint;
        this.identifier = identifier;
        this.exception = "";
    }

    @Override
    public String getEndpoint() {
        return this.endpoint;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     *
     * @return
     */
    @Override
    public String getServerId() {
        if (this.serverid.length() == 0) {
            String uri = this.endpoint;
            uri = uri.replace(IRichWPSProvider.DEFAULT_WPST_ENDPOINT, IRichWPSProvider.DEFAULT_WPS_ENDPOINT);
            return uri;
        }
        return serverid;
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

    @Override
    public String getProcessversion() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getAbstract() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IInputSpecifier> getInputs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IOutputSpecifier> getOutputs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
