package de.hsos.richwps.mb.richWPS.entity.impl.arguments;

import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputBoundingBoxDataSpecifier;

/**
 *
 * @author caduevel
 */
public class OutputBoundingBoxDataArgument implements IOutputArgument {

    private OutputBoundingBoxDataSpecifier specifier;

    /**
     *
     * @param specifier
     */
    public OutputBoundingBoxDataArgument(
            OutputBoundingBoxDataSpecifier specifier) {
        this.specifier = specifier;
    }
    
    /**
     *
     * @return Unambiguous identifier or name of a process, input, or
     * output, unique for this server.
     */
    public String getIdentifier() {
        return this.specifier.getIdentifier();
    }
    
    /**
     *
     * @return
     */
    public OutputBoundingBoxDataSpecifier getSpecifier(){
        return this.specifier;
    }
}
