package de.hsos.richwps.mb.richWPS.entity;

import net.opengis.wps.x100.OutputDescriptionType;

/**
 *
 * @author dalcacer
 */
public interface IOutputSpecifier {
    
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
    public OutputDescriptionType toOutputDescription();
    
}
