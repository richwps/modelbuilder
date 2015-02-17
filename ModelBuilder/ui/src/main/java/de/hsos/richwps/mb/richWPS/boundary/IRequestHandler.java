package de.hsos.richwps.mb.richWPS.boundary;

/**
 * Interface for handling RequestObjects.
 *
 * @see de.hsos.richwps.mb.richWPS.entity.impl.
 *
 * @author dalcacer
 * @version 0.0.1
 */
public interface IRequestHandler {

    /**
     * Handles a request.
     */
    public void handle();

    /**
     * Performs a preview.
     */
    public String preview();
}
