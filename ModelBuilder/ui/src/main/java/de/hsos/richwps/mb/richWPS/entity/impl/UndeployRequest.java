package de.hsos.richwps.mb.richWPS.entity.impl;

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

    private String endpoint = "";
    private String identifier = "";
    /**
     * Exception instead of result.
     */
    private boolean wasException = false;
    /**
     * Exception text.
     */
    private String exception = "";

    public UndeployRequest(String endpoint, String identifier) {
        this.endpoint = endpoint;
        this.identifier = identifier;
        this.exception="";
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
        this.exception+=message;
    
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
