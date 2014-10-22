package de.hsos.richwps.mb.properties;

/**
 *
 * @author dziegenh
 */
public interface IPropertyChangeListener {
    
    public enum PropertyChangeType {
        VALUE_CHANGED,
        POSSIBLE_VALUES_CHANGED
    };
    
    public void propertyChanged(PropertyChangeType changeType);
}
