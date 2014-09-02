package de.hsos.richwps.mb.richWPS.entity.impl.arguments;

import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputLiteralDataSpecifier;

/**
 *
 * @author dalcacer
 */
public class OutputLiteralDataArgument implements IOutputArgument {

    private OutputLiteralDataSpecifier specifier;

    /**
     *
     * @param specifier
     */
    public OutputLiteralDataArgument(OutputLiteralDataSpecifier specifier) {
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
    public OutputLiteralDataSpecifier getSepcifier(){
        return this.specifier;
    }
    
}
