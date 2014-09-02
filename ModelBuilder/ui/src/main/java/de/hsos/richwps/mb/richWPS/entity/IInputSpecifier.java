package de.hsos.richwps.mb.richWPS.entity;

import net.opengis.wps.x100.InputDescriptionType;

/**
 *
 * @author dalcacer
 */
public interface IInputSpecifier {
    
    public String getIdentifier();
    
    public String getAbstract();
    
    public String getTitle();
    
    public int getMinOccur();
    
    public int getMaxOccur();
    
    public InputDescriptionType toInputDescription();
}
