package de.hsos.richwps.mb.richWPS.entity.impl.values;

import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputLiteralDataDescription;

/**
 *
 * @author dalcacer
 */
public class OutputLiteralDataValue implements IOutputArgument{

    
    private OutputLiteralDataDescription specifier;

    /**
     *
     * @param specifier
     */
    public OutputLiteralDataValue(OutputLiteralDataDescription specifier) {
        this.specifier = specifier;
    }

    /**
     *
     * @return
     */
    public String getIdentifier() {
        return this.specifier.getIdentifier();
    }
    
    /**
     *
     * @return
     */
    public OutputLiteralDataDescription getSepcifier(){
        return this.specifier;
    }
    
}
