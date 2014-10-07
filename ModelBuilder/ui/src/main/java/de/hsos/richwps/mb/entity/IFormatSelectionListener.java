package de.hsos.richwps.mb.entity;

/**
 * Can be implemented in order to be notified if a port's complex datatype
 * format has changed.
 *
 * @author dziegenh
 */
public interface IFormatSelectionListener {

    void formatSelected(ComplexDataTypeFormat format);
}
