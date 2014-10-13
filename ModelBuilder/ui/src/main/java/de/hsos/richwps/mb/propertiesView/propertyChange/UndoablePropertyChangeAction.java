package de.hsos.richwps.mb.propertiesView.propertyChange;

import de.hsos.richwps.mb.properties.AbstractPropertyComponent;
import java.util.LinkedList;
import java.util.List;

/**
 * Undoable action for property value changes.
 *
 * @author dziegenh
 */
public class UndoablePropertyChangeAction {

    private AbstractPropertyComponent component;

    private PropertyChangeEvent event;

    private Object oldValue;

    private Object newValue;

    public UndoablePropertyChangeAction(AbstractPropertyComponent component, PropertyChangeEvent event, Object oldValue) {
        this.component = component;
        this.event = event;
        this.oldValue = oldValue;
        this.newValue = event.getNewValue();
    }

    public void undo() {
        component.setValue(oldValue);
        event = new PropertyChangeEvent(event.getSourceCard(), event.getProperty(), event.getSourceObject(), oldValue);
        firePropertyChange();
    }

    public void redo() {
        component.setValue(newValue);
        event = new PropertyChangeEvent(event.getSourceCard(), event.getProperty(), event.getSourceObject(), newValue);
        firePropertyChange();
    }

    public void addChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    private List<PropertyChangeListener> listeners = new LinkedList<>();

    private void firePropertyChange() {
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }
    }

}
