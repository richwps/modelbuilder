package de.hsos.richwps.mb.richWPS.entity;

import java.io.Serializable;
import net.opengis.wps.x100.InputDescriptionType;

/**
 * 
 * @author dalcacer
 */
public interface IInputDescription extends Serializable { 
    
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
     *
     * @return  Title of a process, input, or output, normally available for 
     *          display to a human.
     */
    public String getTitle();
    
    /**
     * @return  Minimum number of times that values for this parameter are
     *          required.
     */
    public int getMinOccur();
    
    /**
     * @return  maxOccurs Maximum number of times that this parameter may
     *          be present.
     */
    public int getMaxOccur();
    
    /**
     *
     * @return An XML InputDescriptionType
     * @see <a href="http://www.opengis.net/wps/1.0.0">
     *          http://www.opengis.net/wps/1.0.0
     *      </a>.
     */
    public InputDescriptionType toInputDescription();
}
