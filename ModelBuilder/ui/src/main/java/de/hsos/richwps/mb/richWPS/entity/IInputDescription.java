package de.hsos.richwps.mb.richWPS.entity;

import java.io.Serializable;
import net.opengis.wps.x100.InputDescriptionType;

/**
 *
 * @author dalcacer
 */
public interface IInputDescription extends Serializable { 
    
    /**
     *
     * @return
     */
    public String getIdentifier();
    
    /**
     *
     * @return
     */
    public String getAbstract();
    
    /**
     *
     * @return
     */
    public String getTitle();
    
    /**
     *
     * @return
     */
    public int getMinOccur();
    
    /**
     *
     * @return
     */
    public int getMaxOccur();
    
    /**
     *
     * @return
     */
    public InputDescriptionType toInputDescription();
}
