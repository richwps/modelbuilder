package de.hsos.richwps.mb.richWPS.entity;

import org.n52.wps.client.transactional.BasicOutputDescriptionType;

/**
 *
 * @author dalcacer
 */
public interface IOutputSpecifier {
    
    public String getIdentifier();
    
    public String getAbstract();
    
    public String getTitle();
    
    public BasicOutputDescriptionType toBasicOutputDescriptionType();
    
}
