package de.hsos.richwps.mb.richWPS.entity.execute;

import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.specifier.OutputLiteralDataSpecifier;

/**
 *
 * @author dalcacer
 */
public class OutputLiteralDataArgument implements IOutputArgument {

    private OutputLiteralDataSpecifier specifier;

   
    public OutputLiteralDataArgument(OutputLiteralDataSpecifier specifier) {
        this.specifier = specifier;
    }

    public String getIdentifier() {
        return this.specifier.getIdentifier();
    }
    
}
