package de.hsos.richwps.mb.richWPS.entity.impl.values;

import de.hsos.richwps.mb.richWPS.entity.IOutputDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputBoundingBoxDataDescription;

/**
 *
 * @author caduevel
 */
public class OutputBoundingBoxDataValue implements IOutputDescription {

    private OutputBoundingBoxDataDescription description;

    /**
     *
     * @param description
     */
    public OutputBoundingBoxDataValue(
            OutputBoundingBoxDataDescription description) {
        this.description = description;
    }
    
    /**
     *
     * @return Unambiguous identifier or name of a process, input, or
     * output, unique for this server.
     */
    public String getIdentifier() {
        return this.description.getIdentifier();
    }
    
    /**
     *
     * @return
     */
    public OutputBoundingBoxDataDescription getDescription(){
        return this.description;
    }
}
