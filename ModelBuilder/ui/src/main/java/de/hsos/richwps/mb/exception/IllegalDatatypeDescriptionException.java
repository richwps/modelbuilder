package de.hsos.richwps.mb.exception;

import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.entity.datatypes.IDataTypeDescription;

/**
 *
 * @author dziegenh
 */
public class IllegalDatatypeDescriptionException extends Exception {

    public IllegalDatatypeDescriptionException(ProcessPortDatatype datatype, IDataTypeDescription dataTypeDescription) {
        super("" + dataTypeDescription.getClass().getSimpleName() + " is no valid description type for datatype " + datatype.toString());
    }

}
