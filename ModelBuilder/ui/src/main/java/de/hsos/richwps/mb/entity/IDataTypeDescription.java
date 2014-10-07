package de.hsos.richwps.mb.entity;

/**
 * Interface for the description of different datatypes; used by input and
 * output ports.
 *
 * @author dziegenh
 */
public interface IDataTypeDescription {

    boolean isDescriptionFor(ProcessPortDatatype dataType);

}
