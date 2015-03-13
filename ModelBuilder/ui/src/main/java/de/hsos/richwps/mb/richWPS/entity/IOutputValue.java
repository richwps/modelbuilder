package de.hsos.richwps.mb.richWPS.entity;

import java.io.Serializable;
import net.opengis.wps.x100.OutputDescriptionType;

/**
 *
 * @author dalcacer
 */
public interface IOutputValue extends Serializable {

    /**
     * @return  Unambiguous identifier or name of a process, input, or output,
     *          unique for this server.
     */
    public String getIdentifier();

    /**
     * @return  AbstractText. Brief narrative description
     *          of a process, input, or output, normally available for display
     *          to a human.
     */
    public String getAbstract();

    /**
     * @return  Title of a process, input, or output, normally available for 
     *          display to a human.
     */
    public String getTitle();

    /**
     *
     * @return An XML OutputDescriptionType
     * @see <a href="http://www.opengis.net/wps/1.0.0">
     *          http://www.opengis.net/wps/1.0.0
     *      </a>.
     */
    public OutputDescriptionType toOutputDescription();

}
