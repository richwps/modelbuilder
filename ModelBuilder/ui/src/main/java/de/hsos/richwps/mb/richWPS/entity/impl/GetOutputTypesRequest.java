package de.hsos.richwps.mb.richWPS.entity.impl;

import de.hsos.richwps.mb.richWPS.entity.IInputDescription;
import de.hsos.richwps.mb.richWPS.entity.IOutputValue;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class GetOutputTypesRequest implements IRequest {
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
     * 
     */
    private List<List<String>> formats;

    /**
     * The corresponding serverid (wps-endpoint).
     */
    private String serverid = "";
    
    public GetOutputTypesRequest(final String wpsurl, final String richwpsurl){
        this.endpoint=richwpsurl;
        this.serverid=wpsurl;
        this.formats=new  LinkedList<>();
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
    public List<IInputDescription> getInputs() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public List<IOutputValue> getOutputs() {
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
        this.endpoint=end;
    }

    @Override
    public void setIdentifier(String id) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    public List<List<String>> getFormats() {
        return formats;
    }

    public void setFormats(List<List<String>> formats) {
        this.formats = formats;
    }
    
    
}
