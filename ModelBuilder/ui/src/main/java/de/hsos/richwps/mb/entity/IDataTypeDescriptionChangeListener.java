package de.hsos.richwps.mb.entity;

/**
 * Can be implemented in order to be notified if a port's complex datatype
 * description has changed.
 *
 * @author dziegenh
 */
public interface IDataTypeDescriptionChangeListener {

    void dataTypeDescriptionChanged(DataTypeDescriptionComplex dataTypeDescription);
    
}
