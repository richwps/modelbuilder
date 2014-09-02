package de.hsos.richwps.mb.richWPS.entity;

import java.util.List;

/**
 *
 * @author dalcacer
 */
public interface IRequest {
    
    /**
     *
     * @return
     */
    public String getEndpoint();

    /**
     *
     * @return
     */
    public String getIdentifier();

    /**
     *
     * @return
     */
    public String getProcessversion();
    
        /**
     *
     * @return
     */
    public String getAbstract();

    /**
     *
     * @return
     */
    public List<IInputSpecifier> getInputs();

    /**
     *
     * @return
     */
    public List<IOutputSpecifier> getOutputs();

    /**
     *
     * @return
     */
    public boolean isException();

    /**
     *
     * @return
     */
    public String getException();

    /**
     *
     * @param message
     */
    public void addException(final String message);
        
}
