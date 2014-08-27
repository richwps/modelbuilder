/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.propertiesView.propertyChange;

import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author dziegenh
 */
public class UndoablePropertyChangeAction {

    private AbstractPropertyComponent component;

    private PropertyChangeEvent event;

    private Object oldValue;

    private Object newValue;

    public UndoablePropertyChangeAction(AbstractPropertyComponent component, PropertyChangeEvent event, Object oldValue) {
//    public UndoablePropertyChangeAction(AbstractPropertyComponent component, Object oldValue, Object newValue) {
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
        for(PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }
    }

}
