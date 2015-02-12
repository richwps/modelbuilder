package de.hsos.richwps.mb.richWPS.entity.impl.values;

import de.hsos.richwps.mb.richWPS.entity.IOutputDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputLiteralDataDescription;

/**
 *
 * @author dalcacer
 */
public class OutputLiteralDataValue implements IOutputDescription{

    
    private OutputLiteralDataDescription description;

    /**
     *
     * @param description
     */
    public OutputLiteralDataValue(OutputLiteralDataDescription description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public String getIdentifier() {
        return this.description.getIdentifier();
    }
    
    /**
     *
     * @return
     */
    public OutputLiteralDataDescription getDescription(){
        return this.description;
    }
    
}
