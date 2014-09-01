package de.hsos.richwps.mb.richWPS.entity;

import org.n52.wps.client.transactional.BasicInputDescriptionType;

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
    
    public BasicInputDescriptionType toBasicInputDescriptionType();
}
