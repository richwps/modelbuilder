package de.hsos.richwps.mb.entity.oldVersions;

import de.hsos.richwps.mb.entity.ProcessPortDatatype;

/**
 * Interface for the description of different datatypes; used by input and
 * output ports.
 *
 * @author dziegenh
 */
public interface IDataTypeDescription {

    boolean isDescriptionFor(ProcessPortDatatype dataType);

}
