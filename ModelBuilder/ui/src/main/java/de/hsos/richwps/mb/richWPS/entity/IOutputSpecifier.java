package de.hsos.richwps.mb.richWPS.entity;

import net.opengis.wps.x100.OutputDescriptionType;

/**
 *
 * @author dalcacer
 */
public interface IOutputSpecifier {
    
    public String getIdentifier();
    
    public String getAbstract();
    
    public String getTitle();
    
    public OutputDescriptionType toOutputDescription();
    
}
