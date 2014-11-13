package de.hsos.richwps.mb.richWPS.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Interface that represents an wps:/wpst:-request.
 *
 * @author dalcacer
 */
public interface IRequest extends Serializable {

    /**
     * The endpoint the request should be directed to.
     *
     * @return endpoint the request should be directed to.
     */
    public String getEndpoint();

    /**
     * In context of wps: the processidentifier which should be used.
     *
     * @return processidentifier which should be used.
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

    /**
     *
     */
    public void flushException();

    /**
     *
     */
    public void flushResults();

    /**
     *
     */
    public String getServerId();

    /**
     * Indicator that this request is fully loaded and ready for execution.
     *
     * @return
     */
    public boolean isLoaded();
}
