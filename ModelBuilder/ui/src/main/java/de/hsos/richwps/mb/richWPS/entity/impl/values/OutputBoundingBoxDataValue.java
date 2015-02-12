package de.hsos.richwps.mb.richWPS.entity.impl.values;

import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputBoundingBoxDataDescription;

/**
 *
 * @author caduevel
 */
public class OutputBoundingBoxDataValue implements IOutputArgument {

    private OutputBoundingBoxDataDescription specifier;

    /**
     *
     * @param specifier
     */
    public OutputBoundingBoxDataValue(
            OutputBoundingBoxDataDescription specifier) {
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
    public OutputBoundingBoxDataDescription getSpecifier(){
        return this.specifier;
    }
}
